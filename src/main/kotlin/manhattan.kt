import java.io.File
import java.lang.Math.abs
import java.nio.charset.Charset

data class Point(val x: Int, val y:Int)
data class PointNearest(val x: Int, val y: Int, val nearestTarget: Int)

fun getGrid(targets: List<Point>): Point {
    val x = targets.maxBy {it.x}?.x?:0
    val y = targets.maxBy {it.y}?.y?:0
    return Point(x, y)
}

fun loadTargets(fileName :String) :List<Point> {
    val targetsString = File(object {}.javaClass.getResource(fileName).toURI()).readLines(Charset.defaultCharset())
    val regex = """(\d+), (\d+)""".toRegex()
    return targetsString.map {regex.find(it)}.map {getPoint(it)}
}

private fun getPoint(result: MatchResult?): Point {
    val (x, y) = result!!.destructured
    return Point(x.toInt(), y.toInt())
}

fun getDistance(first: Point, second: Point): Int {
    return abs(first.x - second.x) + abs(first.y - second.y)
}

fun getNeighbours(target: Point, distance: Int): List<Point> {
    val startX = target.x - distance
    val endX = target.x + distance
    val startY = target.y - distance
    val endY = target.y + distance
    val result = mutableListOf<Point>()
    (startX..endX).forEach {x ->
        (startY..endY).forEach {y ->
            if (getDistance(target, Point(x, y)) == distance) {
                result.add(Point(x, y))
            }
        }
    }
    return result
}

fun getNeighboursInGrid(grid: Point, target: Point, distance: Int): List<Point> {
    return getNeighbours(target, distance).filter {it.x <= grid.x && it.y <= grid.y}
}

fun getNearestTargets(grid: Point, targets: List<Point>): List<PointNearest> {
    val result = mutableListOf<PointNearest>()
    (0..grid.x).forEach { x->
        (0..grid.y).forEach { y->
            val point = Point(x, y)
            if (targets.contains(point)) {
                result.add(PointNearest(x, y, targets.indexOf(point)))
            } else {
                for (distance in (1..(grid.x + grid.y))) {
                    val nearTargets = getNeighboursInGrid(grid, point, distance).filter { targets.contains(it) }
                    if (nearTargets.size == 1) {
                        result.add(PointNearest(x, y, targets.indexOf(nearTargets[0])))
                        break
                    } else if (nearTargets.size > 1) {
                        result.add(PointNearest(x, y, -1))
                        break
                    }
                }
            }
        }
    }
    return result
}

fun getInfiniteTargets(grid: Point, pointNearests: List<PointNearest>): List<Int> {
    val targets = mutableListOf<Int>()
    targets.addAll(pointNearests.filter {it.x == 0}.filter {it.nearestTarget != -1}.map {it.nearestTarget}.distinct())
    targets.addAll(pointNearests.filter {it.y == 0}.filter {it.nearestTarget != -1}.map {it.nearestTarget}.distinct())
    targets.addAll(pointNearests.filter {it.x == grid.x}.filter {it.nearestTarget != -1}.map {it.nearestTarget}.distinct())
    targets.addAll(pointNearests.filter {it.y == grid.y}.filter {it.nearestTarget != -1}.map {it.nearestTarget}.distinct())
    return targets.distinct()
}

fun getFiniteTargetsMaxSize(targets: List<Point>): Int {
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
