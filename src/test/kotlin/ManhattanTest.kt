import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.junit.Ignore
import org.junit.Test

class ManhattanTest {

    @Test
    fun getGrid_singleTarget() {
        assertThat(getGrid(listOf(Target(0, Point(1, 1)))), `is`(Grid(Point(1, 1))))
    }

    @Test
    fun getGrid_2Targets() {
        assertThat(getGrid(listOf(Target(0, Point(1, 1)), Target(1, Point(1, 2)))), `is`(Grid(Point(1, 2))))
    }

    @Test
    fun loadTargets_example() {
        val expected = listOf(
            Target(0, Point(1, 1)),
            Target(1, Point(1, 6)),
            Target(2, Point(8, 3)),
            Target(3, Point(3, 4)),
            Target(4, Point(5, 5)),
            Target(5, Point(8, 9))
        )
        assertThat(loadTargets("example.txt"), `is`(expected))
    }

    @Test
    fun getGrid_manyTargets() {
        assertThat(getGrid(loadTargets("example.txt")), `is`(Grid(Point(8, 9))))
    }

    @Test
    fun getDistance_zero() {
        assertThat(getDistance(Point(1, 1), Point(1, 1)), `is`(0))
    }

    @Test
    fun getDistance_one() {
        assertThat(getDistance(Point(1, 1), Point(1, 2)), `is`(1))
    }

    @Test
    fun getDistance_negativeOne() {
        assertThat(getDistance(Point(1, 1), Point(1, 0)), `is`(1))
    }

    @Test
    fun getDistance_two() {
        assertThat(getDistance(Point(1, 1), Point(2, 2)), `is`(2))
    }

    @Test
    fun getDistance_ten() {
        assertThat(getDistance(Point(1, 1), Point(4, 8)), `is`(10))
    }

    @Test
    fun getNeighbours_length0() {
        val expected = listOf(Point(0, 0))
        assertThat(getNeighbours(Point(0, 0), 0), containsInAnyOrder(*expected.toTypedArray()))
    }

    @Test
    fun getNeighbours_length1() {
        val expected = listOf(Point(0, 1), Point(1, 0), Point(2, 1), Point(1, 2))
        assertThat(getNeighbours(Point(1, 1), 1), containsInAnyOrder(*expected.toTypedArray()))
    }

    @Test
    fun getNeighbours_length2() {
        val expected = listOf(Point(0, 2), Point(1, 1), Point(1, 3), Point(2, 0),
            Point(2, 4), Point(3, 1), Point(3, 3), Point(4, 2))
        assertThat(getNeighbours(Point(2, 2), 2), containsInAnyOrder(*expected.toTypedArray()))
    }

    @Test
    fun getNeighboursInGrid_length2() {
        val expected = listOf(Point(0, 2), Point(1, 1), Point(1, 3), Point(2, 0),
            Point(3, 1), Point(3, 3))
        assertThat(getNeighboursInGrid(Grid(Point(3, 3)), Point(2, 2), 2), containsInAnyOrder(*expected.toTypedArray()))
    }

    @Test
    fun getNearestTargets_simple() {
        val expected = listOf(PointNearest(0, 0, 0),
            PointNearest(0, 1, 0),
            PointNearest(1, 0, 0),
            PointNearest(1, 1, 0)
        )
        assertThat(getNearestTargets(Grid(Point(1, 1)), listOf(Target(0, Point(1, 1)))), containsInAnyOrder(*expected.toTypedArray()))
    }

    @Test
    fun getNearestTargets_harder() {
        val expected = listOf(PointNearest(0, 0, 0),
            PointNearest(0, 1, 0),
            PointNearest(0, 2, -1),
            PointNearest(1, 0, 0),
            PointNearest(1, 1, -1),
            PointNearest(1, 2, 1),
            PointNearest(2, 0, -1),
            PointNearest(2, 1, 1),
            PointNearest(2, 2, 1)
        )
        assertThat(getNearestTargets(Grid(Point(2, 2)), listOf(Target(0, Point(0, 0)), Target(1, Point(2, 2)))), containsInAnyOrder(*expected.toTypedArray()))
    }

    @Test
    fun getInfiniteTargets_simple() {
        val expected = listOf(0, 1)
        val grid = Grid(Point(2, 2))
        val allPoints = listOf(PointNearest(0, 0, 0),
            PointNearest(0, 1, 0),
            PointNearest(0, 2, -1),
            PointNearest(1, 0, 0),
            PointNearest(1, 1, -1),
            PointNearest(1, 2, 1),
            PointNearest(2, 0, -1),
            PointNearest(2, 1, 1),
            PointNearest(2, 2, 1)
            )
        assertThat(getInfiniteTargets(grid, allPoints), `is`(expected))
    }

    @Test
    fun getInfiniteTargets_harder() {
        val expected = listOf(2)
        val grid = Grid(Point(3, 3))
        // below are the results of a 4x4 grid with targets 0=0,0, 1=1,1, 2=3,3
        val allPoints = listOf(
            PointNearest(0, 0, -1),
            PointNearest(0, 1, -1),
            PointNearest(0, 2, -1),
            PointNearest(0, 3, -1),
            PointNearest(1, 0, -1),
            PointNearest(1, 1, -1),
            PointNearest(1, 2, 1),
            PointNearest(1, 3, -1),
            PointNearest(2, 0, -1),
            PointNearest(2, 1, 1),
            PointNearest(2, 2, -1),
            PointNearest(2, 3, 2),
            PointNearest(3, 0, -1),
            PointNearest(3, 1, -1),
            PointNearest(3, 2, 2),
            PointNearest(3, 3, -1)
        )
        assertThat(getInfiniteTargets(grid, allPoints), `is`(expected))
    }

    @Test
    fun getFiniteTargetSize_simple() {
        val targets = listOf(
            Target(0, Point(0, 0)),
            Target(1, Point(1, 1)),
            Target(2, Point(3, 3))
        )
        assertThat(getFiniteTargetsMaxSize(targets), `is`(3))
    }

    @Test
    fun getFiniteTargets_example() {
        assertThat(getFiniteTargetsMaxSize(loadTargets("example.txt")), `is`(17))
    }

    @Test
    @Ignore
    fun getFiniteTargets_part1() {
        assertThat(getFiniteTargetsMaxSize(loadTargets("puzzleInput.txt")), `is`(5358))
    }
}