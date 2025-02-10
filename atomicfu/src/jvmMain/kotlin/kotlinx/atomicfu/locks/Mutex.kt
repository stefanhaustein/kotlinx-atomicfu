package kotlinx.atomicfu.locks

/**
 * This mutex uses a [ReentrantLock].
 * 
 * [getReentrantLock] obtains the actual [ReentrantLock].
 * Construct with `Mutex(reentrantLock)` to create a [Mutex] that uses an existing instance of [ReentrantLock].
 */
actual class Mutex(private val reentrantLock: java.util.concurrent.locks.ReentrantLock) {
    actual constructor(): this(ReentrantLock())
    actual fun isLocked(): Boolean = reentrantLock.isLocked
    actual fun tryLock(): Boolean = reentrantLock.tryLock()
    actual fun lock() = reentrantLock.lock()
    actual fun unlock() = reentrantLock.unlock()

    /**
     * @return the underlying [ReentrantLock]
     */
    fun getReentrantLock(): ReentrantLock = reentrantLock
}