package net.dean.jraw.test.unit

import com.winterbe.expekt.should
import net.dean.jraw.pagination.RotatingSearchList
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class RotatingSearchListTest : Spek({
    lateinit var list: RotatingSearchList<Int>

    beforeEachTest {
        list = RotatingSearchList(capacity = 5)
    }

    describe("add") {
        it("should add data to the front once the capacity is full") {
            for (i in 0 until list.capacity) {
                list.currentIndex.should.equal(i)
                list.size.should.equal(i)

                // add() should always return null here because we haven't had to overwrite any other data
                list.add(i).should.be.`null`
            }

            // Filled the entire array, next index to be replaced should be the oldest data
            list.size.should.equal(list.capacity)
            list.currentIndex.should.equal(0)

            // Make sure we've filled up the entire array
            list.backingArray.toList().should.equal(listOf(0, 1, 2, 3, 4))

            // Add one more element to the list, making sure it overwrites the beginning of the array
            list.add(42).should.equal(0)
            list.backingArray.toList().should.equal(listOf(42, 1, 2, 3, 4))
        }
    }

    describe("contains") {
        it("should report true only if the element is in the backing array") {
            for (i in 0 until list.capacity) {
                list.add(i)

                for (j in 0..i) {
                    list.contains(i).should.be.`true`
                }
            }

            list.add(42)
            list.contains(42).should.be.`true`

            // We've overwritten 0 with 42, 0 should no longer be in the array
            list.contains(0).should.be.`false`
        }
    }
})
