package kotlinx.atomicfu.parking

import kotlinx.atomicfu.atomic

internal fun testThread(doConcurrent: () ->  Unit): TestThread = TestThread { doConcurrent() }

internal expect class TestThread(toDo: () -> Unit) {
    fun join()
}

internal expect fun sleepMills(millis: Long)

internal class Fut(private val block: () -> Unit) {
    private var thread: TestThread? = null
    private val atomicError = atomic<Throwable?>(null)
    val done = atomic(false)
    init {
        val th = testThread {
            try { block() }
            catch (t: Throwable) {
                atomicError.value = t
                throw t
            }
            finally { done.value = true }
        }
        thread = th
    }
    fun waitThrowing() {
        thread!!.join()
        throwIfError()
    }

    fun throwIfError() = atomicError.value?.let { throw it }

    companion object {
        fun waitAllAndThrow(futs: List<Fut>) {
            while(futs.any { !it.done.value }) {
                sleepMills(1000)
                futs.forEach { it.throwIfError() }
            }
        }
    }

}
