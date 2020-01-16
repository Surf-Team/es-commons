package ru.es.util;

import javafx.geometry.Point2D;

/**
 * Created by saniller on 15.03.2017.
 */
public class Utils2D
{
    public static double getDistance(double[] locA, double[] locB)
    {
        return Math.sqrt(Math.pow(locB[0] - locA[0], 2) + Math.pow(locB[1] - locA[1], 2));
    }

    public static double getDistance(Point2D locA, Point2D locB)
    {
        return Math.sqrt(Math.pow(locB.getX() - locA.getX(), 2) + Math.pow(locB.getY() - locA.getY(), 2));
    }

    //                           * B
    //                        *  *   *
    //                     *     *
    //                  *        *       *
    //               *           *
    //            *              *            *
    //         *              90 *
    //    C *  *  *  *  *  *  *  * D *  *  *  *  * A

    public static double getHeightFromB(double angleBCA, double[] locB, double[] locC)
    {
        return Math.sin(angleBCA) * getDistance(locB, locC);
    }

    public static double getHeightFromB(double angleBCA, double distanceBC)
    {
        return Math.sin(angleBCA) * distanceBC;
    }

    public static Point2D getPointOnLine(Point2D pointA, Point2D pointC, double distanceFromPointA)
    {
        double fullDistance = getDistance(pointA, pointC);

        double distMult = distanceFromPointA / fullDistance;

        double newX = pointA.getX() - distMult * (pointA.getX() - pointC.getX());
        double newY = pointA.getY() - distMult * (pointA.getY() - pointC.getY());
        return new Point2D(newX, newY);
    }

    public static double getDistMult(Point2D pointA, Point2D pointC, double distanceFromPointA)
    {
        double fullDistance = getDistance(pointA, pointC);

        return distanceFromPointA / fullDistance;
    }

                                                                     // mouse
    public static Point2D getHeightPointFromB(Point2D pointA, Point2D pointB, Point2D pointC)
    {
        // a - start loc
        // b - mouse loc
        // c - endLOc

        double angleACB = getAngle(pointC, pointA, pointB);
        double angleCBD = 90 - angleACB;
        //double distanceAB = getDistance(pointA, pointB);
        //double heightFromB = getHeightFromB(angleACB, angleACB);
        double distanceCD = getDistanceCD(pointC, pointB, angleCBD);
        return getPointOnLine(pointC, pointA, distanceCD);
    }

    // определяем - будет ли точка высоты внутри треугольника
    // pointB - точка, откуда проводим перпендикуляр
    public static boolean heightIsOnVector(Point2D pointA, Point2D pointB, Point2D pointC)
    {
        // a - start loc
        // b - mouse loc
        // c - endLOc

        double angleACB = getAngle(pointC, pointA, pointB);
        double angleCBD = 90 - angleACB;
        //double distanceAB = getDistance(pointA, pointB);
        //double heightFromB = getHeightFromB(angleACB, angleACB);
        double distanceCD = getDistanceCD(pointC, pointB, angleCBD);
        double distMult = getDistMult(pointC, pointA, distanceCD);
        return (distMult <= 1 && distMult >= 0);
    }

                                                                     // mouse

    public static double getDistanceCD(Point2D pointC, Point2D pointB, double angleCBD)
    {
        double distanceCB = getDistance(pointC, pointB);
        double distanceCD = distanceCB * Math.sin(Math.toRadians(angleCBD));

        return distanceCD;
    }

    // проверено. всё работает. Первая точка - центральная (угол которой ищем) последующие - не имеет значение в каком порядке
    public static double getAngle(Point2D midPoint, Point2D a, Point2D b)
    {
        /**double x1 = a.getX() - midPoint.getX();
        double x2 = b.getX() - midPoint.getX();

        double y1 = a.getY() - midPoint.getY();
        double y2 = b.getY() - midPoint.getY();

        double dist1 = Math.sqrt(x1 * x1 + y1 * y1);
        double dist2 = Math.sqrt(x2 * x2 + y2 * y2);**/

        double l1x = midPoint.getX() - a.getX();
        double l1y = midPoint.getY() - a.getY();
        double l2x = b.getX() - midPoint.getX();
        double l2y = b.getY() - midPoint.getY();

        double l1xSQR = l1x * l1x;
        double l1ySQR = l1y * l1y;

        double l2xSQR = l2x * l2x;
        double l2ySQR = l2y * l2y;

        double l1SQRT = Math.sqrt(l1xSQR + l1ySQR);
        double l2SQRT = Math.sqrt(l2xSQR + l2ySQR);


        return 180 - Math.acos(((l1x * l2x) + (l1y * l2y))/(l1SQRT*l2SQRT)) * 180.0 / Math.PI;

        //return Math.acos((x1 * x2 + y1 * y2) / (dist1 * dist2)); // Это радианы
        //return Math.acos((x1 * x2 + y1 * y2) / (dist1 * dist2)) * 180.0 / Math.PI;
        //return Math.acos((x1 * x2 + y1 * y2) / (getDistance(a, midPoint) * getDistance(b, midPoint)));
    }
}
