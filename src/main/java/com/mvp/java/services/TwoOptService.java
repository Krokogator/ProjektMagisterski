package com.mvp.java.services;

import com.mvp.java.model.salesman.City;
import com.mvp.java.model.salesman.Route;
import javafx.geometry.Point2D;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.util.stream.Collectors.toList;

@Service
public class TwoOptService {

    @Async
    public CompletableFuture<Route> optimise(Route route){
        CompletableFuture<Route> future = new CompletableFuture<>();

        Point[] points = route.getCities().stream().map(x -> new Point(x.getX(), x.getY())).toArray(Point[]::new);
        optimise(points);

        List<City> cities = new ArrayList<>();
        for(Point p : points){
            cities.add(new City(p.getX(), p.getY()));
        }


        future.complete(new Route(cities));

        return future;
    }


    private void reverse(final Point[] x, final int from, final int to) {
        for(int i = from, j = to; i < j; i++, j--) {
            final Point tmp = x[i];
            x[i] = x[j];
            x[j] = tmp;
        }
    }

    /**
     * a tour is a circle. wrap around.
     */
    private int wrap(final int i, final int max) {
        return (max+i) % max;
    }

    /**
     * cost of a 2-Opt.
     * cost of replacing existing edges (ab), (cd) with new edges (ac) (bd).
     * returns the delta of a 2-Opt move. a negative delta indicates that
     * performing this 2-Opt will result in a shorter tour, and a positive delta
     * indicates that this 2-Opt will result in a longer tour.
     *
     * this function is the main hotspot in the optimisation. it is not feasible
     * to pre-compute a matrix (a lookup table) for a tour with N cities, since
     * this will be O(N^2) and the most compact representation will be (N^2-N)/2.
     *
     * good optimisation: most of the time the algorithm is evaluating bad moves,
     * in the obvious case where 2 edge exchanges would result in 2 longer
     * edges, avoid 4 square root operations by comparing squares. this results
     * in a 40% speed up in this code.
     */
    private double moveCost(final Point a, final Point b,
                            final Point c, final Point d) {

        // original edges (ab) (cd)
        final double _ab = a._distance(b), _cd = c._distance(d);

        // candidate edges (ac) (bd)
        final double _ac = a._distance(c), _bd = b._distance(d);

        // triangle of inequality: at least 1 edge will be shorter.
        // if both will be longer, there will be no improvement.
        // return a positive delta to indicate no improvement.
        if(_ab < _ac && _cd < _bd)
            return 1;

        // otherwise must calculate distance delta.
        return (Maths.sqrt(_ac) + Maths.sqrt(_bd)) -
                (Maths.sqrt(_ab) + Maths.sqrt(_cd));
    }

    /**
     * set active bits for 4 vertices making up edges ab, cd.
     */
    private void activate(final Point a, final Point b,
                          final Point c, final Point d) {
        a.setActive(true); b.setActive(true);
        c.setActive(true); d.setActive(true);
    }

    /**
     * try to find a move from the current city.
     * given the current city, search for a 2-opt move that will result in
     * an improvement to the tour length. the edge before the current city,
     * (prevPoint,currenPoint) and after (currentPoint,nextPoint) are compared
     * to all over edges (c,d), starting at (c=currentPoint+2, d=currentPoint+3)
     * until an improvement is found.
     */
    private double findMove(final int current, final Point currentPoint,
                            final Point[] points, final int numCities) {

        // previous and next city index and point object.
        final int prev = wrap(current-1, numCities);
        final int next = wrap(current+1, numCities);
        final Point prevPoint = points[prev];
        final Point nextPoint = points[next];

        // iterate through pairs (i,j) where i = current+2 j = current+3
        // until i = current+numCities-2, j = current+numCities-1.
        // if points = {0,1,2,3,4,5,6,7,8,9}, current = 4, this will produce:
        // (6,7) (7,8) (8,9) (9,0) (0,1) (1,2) (2,3)
        for(int i = wrap(current+2, numCities), j = wrap(current+3, numCities);
            j != current;
            i = j, j = wrap(j+1, numCities)) {

            final Point c = points[i];
            final Point d = points[j];

            // previous edge:
            // see if swaping the current 2 edges:
            // (prevPoint, currentPoint) (c, d) to:
            // (prevPoint, c) (currentPoint, d)
            // will result in an improvement. if so, set active bits for
            // the 4 vertices involved and reverse everything between:
            // (currentPoint, c).
            final double delta1 = moveCost(prevPoint, currentPoint, c, d);
            if(delta1 < 0) {
                activate(prevPoint, currentPoint, c, d);
                reverse(points, Math.min(prev, i)+1, Math.max(prev, i));
                return delta1;
            }

            // next edge:
            // see if swaping the current 2 edges:
            // (currentPoint, nextPoint) (c, d) to:
            // (currentPoint, c) (nextPoint, d)
            // will result in an improvement. if so, set active bits for
            // the 4 vertices involved and reverse everything between:
            // (nextPoint, c).
            final double delta2 = moveCost(currentPoint, nextPoint, c, d);
            if(delta2 < 0) {
                activate(currentPoint, nextPoint, c, d);
                reverse(points, Math.min(current, i)+1, Math.max(current, i));
                return delta2;
            }

        }
        return 0.0;
    }

    /**
     * optimise a tour.
     * return a 2-Optimal tour.
     */
    public double optimise(final Point[] points) {

        // total tour distance
        double best = Point.distance(points);

        // total number of cities in the tour
        final int numCities = points.length;

        // numCities - visited = total number of active cities.
        // current = current city being explored.
        int visited = 0, current = 0;

        // terminate when a full rotation of of static order from city 1:N
        // has completed without making a move (when all cities are inactive).
        // the resulting tour (points) will be "2-Optimal" -that is, no further
        // imrovements are possible (local optima).
        while(visited < numCities) {
            final Point currentPoint = points[current];
            if(currentPoint.isActive()) {

                // from the current city, try to find a move.
                final double modified = findMove(current, currentPoint,
                        points, numCities);

                // if a move was found, go to previous city.
                // best is += modified delta.
                if(modified < 0) {
                    current = wrap(current-1, numCities);
                    visited = 0;
                    best += modified;
                    continue;
                }
                currentPoint.setActive(false);
            }

            // if city is inactive or no moves found, go to next city.
            current = wrap(current+1, numCities);
            visited++;
        }
        return best;
    }

}

final class Point {

    private final double x;
    private final double y;
    private boolean active = true;

    public Point(final double x, final double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Euclidean distance.
     * tour wraps around N-1 to 0.
     */
    public static double distance(final Point[] points) {
        final int len = points.length;
        double d = points[len-1].distance(points[0]);
        for(int i = 1; i < len; i++)
            d += points[i-1].distance(points[i]);
        return d;
    }

    /**
     * Euclidean distance.
     */
    public final double distance(final Point to) {
        return Math.sqrt(_distance(to));
    }

    /**
     * compare 2 points.
     * no need to square when comparing.
     * http://en.wikibooks.org/wiki/Algorithms/Distance_approximations
     */
    public final double _distance(final Point to) {
        final double dx = this.x-to.x;
        final double dy = this.y-to.y;
        return (dx*dx)+(dy*dy);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

    public String toString() {
        return x + " " + y;
    }

    public double getX(){ return x; }
    public double getY(){ return y; }
}

final class Maths {


    private static native double sqrtnat(double d);

    /**
     * fast inverse square root.
     * originally from quake 3:
     * http://en.wikipedia.org/wiki/Fast_inverse_square_root
     *
     * works by making a clever guess as to the starting point
     * for newton's method. 1 pass is a good approximation.
     */
    public static double invSqrt(double x) {
        final double xhalf = 0.5d*x;
        long i = Double.doubleToLongBits(x);
        i = 0x5fe6eb50c7b537a9L - (i >> 1);
        x = Double.longBitsToDouble(i);
        x *= (1.5d - xhalf*x*x); // pass 1
        x *= (1.5d - xhalf*x*x); // pass 2
        x *= (1.5d - xhalf*x*x); // pass 3
        x *= (1.5d - xhalf*x*x); // pass 4
        return x;
    }

    public static final double sqrt(final double d) {
        //return Math.sqrt(d);
        //return sqrtnat(d); // no diff (jni overhead.)
        return d*invSqrt(d); // ~10% faster than Math.sqrt.
    }

}