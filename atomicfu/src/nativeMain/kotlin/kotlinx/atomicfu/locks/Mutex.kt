package kotlinx.atomicfu.locks

actual class Mutex {
    private val lock = NativeMutex()
    actual fun isLocked() = lock.isLocked()
    actual fun tryLock() = lock.tryLock()
    actual fun lock() = lock.lock()
    actual fun unlock() = lock.unlock()
}