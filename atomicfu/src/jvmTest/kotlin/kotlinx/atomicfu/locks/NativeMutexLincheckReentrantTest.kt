import kotlinx.atomicfu.locks.Mutex
import org.jetbrains.kotlinx.lincheck.LoggingLevel
import org.jetbrains.kotlinx.lincheck.annotations.Operation
import org.jetbrains.kotlinx.lincheck.check
import org.jetbrains.kotlinx.lincheck.strategy.managed.modelchecking.ModelCheckingOptions
import kotlin.test.Test

class NativeMutexLincheckReentrantTest {
    class Counter {
        @Volatile
        private var value = 0

        fun inc(): Int = ++value
        fun get() = value
    }
    private val lock = Mutex()
    private val counter = Counter()

    @Test
    fun modelCheckingTest(): Unit = ModelCheckingOptions()
        .iterations(3) // Change to 300 for exhaustive testing
        .invocationsPerIteration(10_000)
        .actorsBefore(1)
        .threads(3)
        .actorsPerThread(3)
        .actorsAfter(0)
        .hangingDetectionThreshold(100)
        .logLevel(LoggingLevel.INFO)
        .check(this::class.java)

    @Operation
    fun inc(): Int {
        lock.lock()
        if (!lock.tryLock()) throw IllegalStateException("couldnt reent with trylock")
        if (!lock.tryLock()) throw IllegalStateException("couldnt reent with trylock")
        val result = counter.inc()
        lock.unlock()
        lock.unlock()
        lock.unlock()
        return result
    }

    @Operation
    fun get(): Int {
        lock.lock()
        if (!lock.tryLock()) throw IllegalStateException("couldnt reent with trylock")
        if (!lock.tryLock()) throw IllegalStateException("couldnt reent with trylock")
        val result = counter.get()
        lock.unlock()
        lock.unlock()
        lock.unlock()
        return result
    }
}
