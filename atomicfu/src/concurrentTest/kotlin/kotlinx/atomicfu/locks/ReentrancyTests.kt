package kotlinx.atomicfu.locks

import kotlin.test.Test
import kotlin.test.assertFails

class ReentrancyTests {
    
    @Test
    fun reentrantTestSuccess() {
        val lock = Mutex()
        lock.lock()
        lock.lock()
        lock.unlock()
        lock.unlock()
    }
    
    @Test
    fun reentrantTestFail() {
        val lock = Mutex()
        lock.lock()
        lock.lock()
        lock.unlock()
        lock.unlock()
        assertFails {
            lock.unlock()
        }
    }
}