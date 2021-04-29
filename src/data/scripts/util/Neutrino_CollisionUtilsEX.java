// by Deathfly and Tartiflette
package data.scripts.util;

import com.fs.starfarer.api.combat.CollisionClass;
import com.fs.starfarer.api.combat.ShieldAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import java.awt.geom.Line2D;
import org.lazywizard.lazylib.CollisionUtils;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

public class Neutrino_CollisionUtilsEX {

    /////////////////////////////////////////
    //                                     //
    //             SHIP HIT                //
    //                                     //
    /////////////////////////////////////////
    /**
     * return the collision point of segment segStart to segEnd and a ship (will
     * consider shield).
     *
     * @return the collision point of segment segStart to segEnd and a ship
     * (will consider shield). if segment can not hit the ship, will return
     * null.
     * @param segStart if segStart hit the ship hull or shield, will return
     * segStart.
     * @param accurateShieldEdgeTest use an additional test to check if the
     * segment hit the shield on edge. Set to false if you want a vanilla like
     * behaviour.
     */
    public static Vector2f getShipCollisionPoint(Vector2f segStart, Vector2f segEnd, ShipAPI ship, boolean accurateShieldEdgeTest) {

        // if target can not be hit, return null
        if (ship.getCollisionClass() == CollisionClass.NONE) {
            return null;
        }
        ShieldAPI shield = ship.getShield();

        // Check hit point when shield is off.
        if (shield == null || shield.isOff()) {
            return CollisionUtils.getCollisionPoint(segStart, segEnd, ship);
        } // If ship's shield is on, thing goes complicated...
        else {
            Vector2f circleCenter = shield.getLocation();
            float circleRadius = shield.getRadius();
            // calculate the shield collision point
            Vector2f tmp1 = getCollisionPointOnCircle(segStart, segEnd, circleCenter, circleRadius);
            if (tmp1 != null) {
                // OK! hit the shield in face
                if (shield.isWithinArc(tmp1)) {
                    return tmp1;
                } else {

                    boolean hit = false;
                    Vector2f tmp = new Vector2f(segEnd);

                    //the beam cannot go farther than it's max range or the hull
                    Vector2f hullHit = CollisionUtils.getCollisionPoint(segStart, segEnd, ship);
                    if (hullHit != null) {
                        tmp = hullHit;
                        hit = true;
                    }

                    // if the hit come outside the shield's arc but it hit the shield's "edge", find that point.
                    if (accurateShieldEdgeTest) {
                        Vector2f shieldEdge1 = MathUtils.getPointOnCircumference(circleCenter, circleRadius, MathUtils.clampAngle(shield.getFacing() + shield.getActiveArc() / 2));
                        Vector2f tmp2 = CollisionUtils.getCollisionPoint(segStart, tmp, circleCenter, shieldEdge1);
                        if (tmp2 != null) {
                            tmp = tmp2;
                            hit = true;
                        }
                        Vector2f shieldEdge2 = MathUtils.getPointOnCircumference(circleCenter, circleRadius, MathUtils.clampAngle(shield.getFacing() - shield.getActiveArc() / 2));
                        Vector2f tmp3 = CollisionUtils.getCollisionPoint(segStart, tmp, circleCenter, shieldEdge2);
                        if (tmp3 != null) {
                            tmp = tmp3;
                            hit = true;
                        }
                    }

                    // return null if segment not hit anything.
                    return hit ? tmp : null;
                }
            }
        }
        return null;
    }

    // Just a compatible method.
    /**
     * return the collision point of segment segStart to segEnd and a ship (will
     * consider shield).
     *
     * @return the collision point of segment segStart to segEnd and a ship
     * (will consider shield). if segment can not hit the ship, will return
     * null.
     * @param segStart if segStart hit the ship hull or shield, will return
     * segStart.
     */
    public static Vector2f getShipCollisionPoint(Vector2f segStart, Vector2f segEnd, ShipAPI ship) {
        return getShipCollisionPoint(segStart, segEnd, ship, false);
    }

    /////////////////////////////////////////
    //                                     //
    //       CIRCLE COLLISION POINT        //
    //                                     //
    /////////////////////////////////////////
    /**
     * return the first intersection point of the segment segStart to segEnd and
     * the circle.
     *
     * @return the first intersection point of segment segStart to segEnd and
     * circumference. if segStart is outside the circle and segment can not
     * intersection with the circumference, will return null.
     * @param segStart if segStart is inside the circle, will return segStart.
     */
    public static Vector2f getCollisionPointOnCircle(Vector2f segStart, Vector2f segEnd, Vector2f circleCenter, float circleRadius) {
        if(segStart.equals(segEnd))return null;
        Vector2f startToEnd = Vector2f.sub(segEnd, segStart, null);
        Vector2f startToCenter = Vector2f.sub(circleCenter, segStart, null);
        double ptLineDistSq = (float) Line2D.ptLineDistSq(segStart.x, segStart.y, segEnd.x, segEnd.y, circleCenter.x, circleCenter.y);

        float circleRadiusSq = circleRadius * circleRadius;

        // if lineStart is within the circle, return it directly
        if (startToCenter.lengthSquared() < circleRadiusSq) {
            return segStart;
        }

        // if lineStart is outside the circle and segment can not reach the circumference, return null
        if (ptLineDistSq > circleRadiusSq || startToCenter.length() - circleRadius > startToEnd.length()) {
            return null;
        }

        // calculate the intersection point.
        startToEnd.normalise(startToEnd);
        double dist = Vector2f.dot(startToCenter, startToEnd) - Math.sqrt(circleRadiusSq - ptLineDistSq);
        startToEnd.scale((float) dist);
        return Vector2f.add(segStart, startToEnd, null);
    }

    /**
     * return the first intersection point of the segment segStart to segEnd and
     * the circumference.
     *
     * @return the first intersection point of segment segStart to segEnd and
     * circumference. if segment can not intersection with the circumference,
     * will return null.
     */
    public static Vector2f getCollisionPointOnCircumference(Vector2f segStart, Vector2f segEnd, Vector2f circleCenter, float circleRadius) {

        Vector2f startToEnd = Vector2f.sub(segEnd, segStart, null);
        Vector2f startToCenter = Vector2f.sub(circleCenter, segStart, null);
        double ptLineDistSq = (float) Line2D.ptLineDistSq(segStart.x, segStart.y, segEnd.x, segEnd.y, circleCenter.x, circleCenter.y);
        float circleRadiusSq = circleRadius * circleRadius;
        boolean CoS = false;
        // if lineStart is within the circle, return it directly
        if (startToCenter.lengthSquared() < circleRadiusSq) {
            CoS = true;
        }

        // if lineStart is outside the circle and segment can not reach the circumference, return null
        if (ptLineDistSq > circleRadiusSq || startToCenter.length() - circleRadius > startToEnd.length()) {
            return null;
        }

        // calculate the intersection point.
        startToEnd.normalise(startToEnd);
        double dist;
        if (CoS) {
            dist = Vector2f.dot(startToCenter, startToEnd) + Math.sqrt(circleRadiusSq - ptLineDistSq);
            if (dist < startToEnd.length()) {
                return null;
            }
        } else {
            dist = Vector2f.dot(startToCenter, startToEnd) - Math.sqrt(circleRadiusSq - ptLineDistSq);
        }
        startToEnd.scale((float) dist);
        return Vector2f.add(segStart, startToEnd, null);
    }

    /////////////////////////////////////////
    //                                     //
    //             SHIELD HIT              //
    //                                     //
    /////////////////////////////////////////
    /**
     * SHOULD ONLY BE USED WHEN YOU ONLY NEED CHECK FOR SHIELD COLLISION POINT!
     * if you need the check for a ship hit (considering it's shield), use
     * getShipCollisionPoint instead.
     *
     * @return the collision point of segment segStart to segEnd and ship's
     * shield. if the segment can not hit the shield or the ship has no shield,
     * return null.
     * @param ignoreHull if ignoreHull = flase and the segment hit the ship's
     * hull first, return null.
     * @param segStart if segStart hit the shield, will return segStart.
     * @param accurateShieldEdgeTest use an additional test to check if the
     * segment hit the shield on edge. Set to false if you want a vanilla like
     * behaviour.
     */
    public static Vector2f getShieldCollisionPoint(Vector2f segStart, Vector2f segEnd, ShipAPI ship, boolean ignoreHull, boolean accurateShieldEdgeTest) {
        // if target not shielded, return null
        ShieldAPI shield = ship.getShield();
        if (ship.getCollisionClass() == CollisionClass.NONE || shield == null || shield.isOff()) {
            return null;
        }
        Vector2f circleCenter = shield.getLocation();
        float circleRadius = shield.getRadius();
        // calculate the shield collision point
        Vector2f tmp1 = getCollisionPointOnCircle(segStart, segEnd, circleCenter, circleRadius);
        if (tmp1 != null) {
            // OK! hit the shield in face
            if (shield.isWithinArc(tmp1)) {
                return tmp1;
            } else {
                // if the hit come outside the shield's arc but it hit the shield's "edge", find that point.                

                Vector2f tmp = new Vector2f(segEnd);
                boolean hit = false;
                if (accurateShieldEdgeTest) {
                    Vector2f shieldEdge1 = MathUtils.getPointOnCircumference(circleCenter, circleRadius, MathUtils.clampAngle(shield.getFacing() + shield.getActiveArc() / 2));
                    Vector2f tmp2 = CollisionUtils.getCollisionPoint(segStart, tmp, circleCenter, shieldEdge1);
                    if (tmp2 != null) {
                        tmp = tmp2;
                        hit = true;
                    }

                    Vector2f shieldEdge2 = MathUtils.getPointOnCircumference(circleCenter, circleRadius, MathUtils.clampAngle(shield.getFacing() - shield.getActiveArc() / 2));
                    Vector2f tmp3 = CollisionUtils.getCollisionPoint(segStart, tmp, circleCenter, shieldEdge2);
                    if (tmp3 != null) {
                        tmp = tmp3;
                        hit = true;
                    }
                }
                // If we don't ignore hull hit, check if there is one...
                if (!ignoreHull && CollisionUtils.getCollisionPoint(segStart, tmp, ship) != null) {
                    return null;
                }
                // return null if do not hit shield.
                return hit ? tmp : null;
            }
        }
        return null;
    }

    // Just a compatible method.
    /**
     * SHOULD ONLY BE USED WHEN YOU ONLY NEED CHECK FOR SHIELD COLLISION POINT!
     * if you need the check for a ship hit (considering it's shield), use
     * getShipCollisionPoint instead.
     *
     * @return the collision point of segment segStart to segEnd and ship's
     * shield. if the segment can not hit the shield or the ship has no shield,
     * return null.
     * @param ignoreHull if ignoreHull = flase and the segment hit the ship's
     * hull first, return null.
     * @param segStart if segStart hit the shield, will return segStart.
     */
    public static Vector2f getShieldCollisionPoint(Vector2f segStart, Vector2f segEnd, ShipAPI ship, boolean ignoreHull) {
        return getShieldCollisionPoint(segStart, segEnd, ship, ignoreHull, false);
    }
}
