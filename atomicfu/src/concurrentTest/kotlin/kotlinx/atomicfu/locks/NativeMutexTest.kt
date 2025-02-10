package kotlinx.atomicfu.locks

import kotlinx.atomicfu.parking.sleepMills
import kotlinx.atomicfu.parking.testThread
import kotlin.test.Test
import kotlin.test.assertEquals

class NativeMutexTest {
    
    
    @Test
    fun testNativeMutexSlow() {
        val mutex = NativeMutex()
        val resultList = mutableListOf<String>()

        val fut1 = testThread {  
            repeat(30) { i ->
                mutex.lock()
                resultList.add("a$i")
                sleepMills(100)
                resultList.add("a$i")
                mutex.unlock()
            }
        }

        val fut2 = testThread {
            repeat(30) { i ->
                mutex.lock()
                resultList.add("b$i")
                sleepMills(100)
                resultList.add("b$i")
                mutex.unlock()
            }
        }

        repeat(30) { i ->
            mutex.lock()
            resultList.add("c$i")
            sleepMills(100)
            resultList.add("c$i")
            mutex.unlock()
        }
        fut1.join()
        fut2.join()
        
        resultList.filterIndexed { i, _ -> i % 2 == 0 }
            .zip(resultList.filterIndexed {i, _ -> i % 2 == 1}) { a, b ->
            assertEquals(a, b)
        }
    }

    @Test
    fun testNativeMutexFast() {
        val mutex = Mutex()
        val resultList = mutableListOf<String>()

        val fut1 = testThread {
            repeat(30000) { i ->
                mutex.lock()
                resultList.add("a$i")
                resultList.add("a$i")
                mutex.unlock()
            }
        }

        val fut2 = testThread {
            repeat(30000) { i -> 
                mutex.lock()
                resultList.add("b$i")
                resultList.add("b$i")
                mutex.unlock()
            }
        }

        repeat(30000) { i ->
            mutex.lock()
            resultList.add("c$i")
            resultList.add("c$i")
            mutex.unlock()
        }
        fut1.join()
        fut2.join()
        
        resultList
            .filterIndexed { i, _ -> i % 2 == 0 }
            .zip(resultList.filterIndexed {i, _ -> i % 2 == 1}) { a, b ->
            assertEquals(a, b)
        }
    }
}