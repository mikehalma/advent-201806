import java.io.File
import java.lang.Math.abs
import java.lang.Math.max
import java.nio.charset.Charset

data class Point(val x: Int, val y:Int)
data class PointNearest(val x: Int, val y: Int, val nearestTarget: Int)

fun getGrid(points: List<Point>): Point {
    val x = points.maxBy {it.x}?.x?:0
    val y = points.maxBy {it.y}?.y?:0
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
                result.add(PointNearest(x, y, -1))
            } else {
                for (distance in (1..(grid.x + grid.y))) {
                    val nearTargets = getNeighboursInGrid(grid, point, distance).filter { targets.contains(it) }
                    if (nearTargets.size == 1) {
                        result.add(PointNearest(x, y, targets.indexOf(nearTargets[0])))
                    } else if (nearTargets.size > 1) {
                        result.add(PointNearest(x, y, -1))
                    }
                }
            }
        }
    }
    return result
}
