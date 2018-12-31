import java.io.File
import java.lang.Math.abs
import java.nio.charset.Charset

data class Point(val x: Int, val y:Int)
data class Target(val id: Int, val point: Point)
data class Grid(val maxPoint: Point) {
    fun inGrid(point: Point): Boolean {
        return when {
            point.x > this.maxPoint.x -> false
            point.y > this.maxPoint.y -> false
            else -> true
        }
    }
}
data class PointNearest(val x: Int, val y: Int, val nearestTarget: Int)

fun getGrid(targets: List<Target>): Grid {
    val x = targets.maxBy {it.point.x}?.point?.x?:0
    val y = targets.maxBy {it.point.y}?.point?.y?:0
    return Grid(Point(x, y))
}

fun loadTargets(fileName :String) :List<Target> {
    val targetsString = File(object {}.javaClass.getResource(fileName).toURI()).readLines(Charset.defaultCharset())
    val regex = """(\d+), (\d+)""".toRegex()
    return targetsString.withIndex()
        .map {(index, targetString) -> Target(index, getPoint(regex.find(targetString)))}
}

private fun getPoint(result: MatchResult?): Point {
    val (x, y) = result!!.destructured
    return Point(x.toInt(), y.toInt())
}

fun getDistance(first: Point, second: Point): Int {
    return abs(first.x - second.x) + abs(first.y - second.y)
}

fun getNeighbours(point: Point, distance: Int): List<Point> {
    val startX = point.x - distance
    val endX = point.x + distance
    val startY = point.y - distance
    val endY = point.y + distance
    return (startX..endX).flatMap {x ->
        (startY..endY).map {y-> Point(x, y)}
            .filter {getDistance(point, it) == distance}
    }
}

fun getNeighboursInGrid(grid: Grid, point: Point, distance: Int): List<Point> {
    return getNeighbours(point, distance).filter {grid.inGrid(it)}
}

fun getNearestTargets(grid: Grid, targets: List<Target>): List<PointNearest> {
    val targetPoints = targets.map {it.point to it.id}.toMap()
    val result = mutableListOf<PointNearest>()
    (0..grid.maxPoint.x).flatMap { x->
        (0..grid.maxPoint.y).map { y->
            addPointNearest(grid, Point(x, y), targetPoints, result)
        }
    }
    return result
}

private fun addPointNearest(
    grid: Grid,
    point: Point,
    targetPoints: Map<Point, Int>,
    result: MutableList<PointNearest>
) {
    result.addAll( (0..(grid.maxPoint.x + grid.maxPoint.y))
        .map {getTargetNeighbours(grid, point, it, targetPoints)}
        .filter { it.isNotEmpty() }
        .take(1)
        .map { if (it.size == 1) targetPoints[it[0]] else -1 }
        .map { PointNearest(point.x, point.y, it?:-1)}

    )

}

private fun getTargetNeighbours(
    grid: Grid,
    point: Point,
    distance: Int,
    targetPoints: Map<Point, Int>
) = getNeighboursInGrid(grid, point, distance).filter { targetPoints.keys.contains(it) }

fun getInfiniteTargets(grid: Grid, pointNearests: List<PointNearest>): List<Int> {

    return listOf(0, grid.maxPoint.x).flatMap {x ->
        listOf(0, grid.maxPoint.y).flatMap {y ->
            pointNearests.filter {it.x == x || it.y == y}
                .filter {it.nearestTarget != -1}
                .map {it.nearestTarget}
        }
    }.distinct()
}

fun getFiniteTargetsMaxSize(targets: List<Target>): Int {
    val grid = getGrid(targets)
    val nearestTargets = getNearestTargets(grid, targets)
    val infiniteTargets = getInfiniteTargets(grid, nearestTargets)
    return nearestTargets
        .filter {it.nearestTarget != -1}
        .map {it.nearestTarget}
        .filter {!infiniteTargets.contains(it)}
        .groupBy {it}
        .maxBy {it.value.size}
        ?.value?.size?:0
}

fun getTotalTargetDistance(point: Point, targets: List<Target>): Int {
    return targets.map {getDistance(it.point, point)}.sum()
}

fun getPointsWithinDistance(targets: List<Target>, distance: Int): List<Point> {
    val grid = getGrid(targets)
    return (0..grid.maxPoint.x).flatMap {x ->
        (0..grid.maxPoint.y)
            .map {y -> Point(x, y)}
            .filter {getTotalTargetDistance(it, targets) < distance}
    }
}
