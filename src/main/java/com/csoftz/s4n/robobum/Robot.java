/*----------------------------------------------------------------------------*/
/* Source File:   ROBOTBUMAPP.JAVA                                            */
/* Copyright (c), 2015, 2022 CSoftZ                                           */
/*----------------------------------------------------------------------------*/
/*-----------------------------------------------------------------------------
 History
 May.27/2015 COQ  File created.
 Feb.22/2022 COQ  Upgraded code style conventions.
 -----------------------------------------------------------------------------*/
package com.csoftz.s4n.robobum;

import java.util.ArrayList;
import java.util.List;

import com.csoftz.s4n.robobum.consts.GlobalConstants;
import com.csoftz.s4n.robobum.domain.FieldThreatLocation;
import com.csoftz.s4n.robobum.domain.RobotPosition;

/**
 * Defines what a robot is exploring
 *
 * @author Carlos Adolfo Ortiz Quirós (COQ)
 * @since 17(JDK)
 */
public class Robot {
    private final String name;
    private final String commands;
    private final String initialPosition;

    private final int maxXAxis;
    private final int maxYAxis;

    private final List<FieldThreatLocation> threatLocs;
    private final List<String> traceList = new ArrayList<>();
    private final List<String> resultList = new ArrayList<>();
    private final List<RobotPosition> robotPosList = new ArrayList<>();

    /**
     * Constructor with params
     *
     * @param threatLocs      A list of field locations.
     * @param name            Gives the Robot a designation.
     * @param initialPosition Establish the initial location the Robot is positioned.
     * @param commands        Sets the instructions the Robot is to execute.
     * @param maxXAxis        The limit in the X-axis.
     * @param maxYAxis        The limit in the Y-axis.
     */
    public Robot(List<FieldThreatLocation> threatLocs, String name,
                 String initialPosition, String commands, int maxXAxis, int maxYAxis) {
        this.threatLocs = threatLocs;
        this.name = name;
        this.commands = commands;
        this.maxXAxis = maxXAxis;
        this.maxYAxis = maxYAxis;
        this.initialPosition = initialPosition;
    }

    /**
     * Analyze commands to locate bombs.
     */
    public void explore() {
        String[] pos = initialPosition.split(" ");
        int currXPos = Integer.parseInt(pos[0]);
        int currYPos = Integer.parseInt(pos[1]);
        char currCardinalPos = pos[2].charAt(0);
        RobotPosition rpos = new RobotPosition(currXPos, currYPos, currCardinalPos);

        robotPosList.add(rpos);

        traceList.add("Robot name: " + name);
        traceList.add("Exploring grid for bomb presence");
        traceList.add("Robot initial position " + "(" + rpos.getX() + ","
            + rpos.getY() + "," + rpos.getCardinalPos() + ")");
        traceList.add("Robot Commands=[" + commands + "]");
        for (int i = 0; i < commands.length(); i++) {
            locateBombAt(rpos);
            char ch = commands.charAt(i);
            switch (ch) {
                case GlobalConstants.ROBOT_COMMAND_MOVE_LEFT:
                    currCardinalPos = rotateLeft(currCardinalPos);
                    rpos = new RobotPosition(currXPos, currYPos, currCardinalPos);
                    break;
                case GlobalConstants.ROBOT_COMMAND_MOVE_RIGHT:
                    currCardinalPos = rotateRight(currCardinalPos);
                    rpos = new RobotPosition(currXPos, currYPos, currCardinalPos);
                    break;
                case GlobalConstants.ROBOT_COMMAND_MOVE_FORWARD:
                    rpos = moveForward(rpos);
                    currCardinalPos = rpos.getCardinalPos();
                    break;
            }
            traceList.add("Executing command [" + ch + "]");
            traceList.add("Affected Robot position " + "(" + rpos.getX() + ","
                + rpos.getY() + "," + rpos.getCardinalPos() + ")");
            robotPosList.add(rpos);
        }
        traceList.add("Finished exploration");
    }

    /**
     * Depending on the given position, it moves one location in the cardinal
     * direction.
     *
     * @param rpos new position.
     */
    private RobotPosition moveForward(final RobotPosition rpos) {
        RobotPosition newpos = new RobotPosition(rpos.getX(), rpos.getY(),
            rpos.getCardinalPos());

        switch (newpos.getCardinalPos()) {
            case GlobalConstants.NORTH_POS:
                int y = newpos.getY() + 1;
                if (y > maxYAxis) {
                    y = maxYAxis;
                }
                newpos.setY(y);
                break;
            case GlobalConstants.SOUTH_POS:
                y = newpos.getY() - 1;
                if (y < 0) {
                    y = 0;
                }
                newpos.setY(y);
                break;
            case GlobalConstants.EAST_POS:
                int x = newpos.getX() + 1;
                if (x > maxXAxis) {
                    x = maxXAxis;
                }
                newpos.setX(x);
                break;
            case GlobalConstants.WEST_POS:
                x = newpos.getX() - 1;
                if (x < 0) {
                    x = 0;
                }
                newpos.setX(x);
                break;
        }
        return newpos;
    }

    /**
     * Given current position examines if there is a bomb there.
     *
     * @param rpos Which location to examine.
     */
    private void locateBombAt(RobotPosition rpos) {
        traceList.add("Locating bomb using coordinates (" + rpos.getX() + ","
            + rpos.getY() + ")");
        boolean found = false;
        char kind = ' ';
        for (FieldThreatLocation ftl : threatLocs) {
            if (ftl.getX() == rpos.getX() && ftl.getY() == rpos.getY()) {
                kind = ftl.getKind();
                found = true;
                break;
            }
        }
        if (found && kind == GlobalConstants.THREAT_BOMB) {
            resultList.add(rpos.getX() + " " + rpos.getY() + " " + rpos.getCardinalPos());
            resultList.add("Threat detected");
            traceList.add("Threat detected at (" + rpos.getX() + "," + rpos.getY() + ")");
        }
    }

    /**
     * Given cardinal position rotates 90º to the left.
     *
     * @param currCardinalPos Current cardinal position
     * @return new cardinal position
     */
    private char rotateLeft(char currCardinalPos) {
        char rslt = currCardinalPos;
        switch (currCardinalPos) {
            case GlobalConstants.NORTH_POS:
                rslt = GlobalConstants.WEST_POS;
                break;
            case GlobalConstants.WEST_POS:
                rslt = GlobalConstants.SOUTH_POS;
                break;
            case GlobalConstants.SOUTH_POS:
                rslt = GlobalConstants.EAST_POS;
                break;
            case GlobalConstants.EAST_POS:
                rslt = GlobalConstants.NORTH_POS;
                break;
        }
        return rslt;
    }

    /**
     * Given cardinal position rotates 90º to the right.
     *
     * @param currCardinalPos Current cardinal position
     * @return new cardinal position
     */
    private char rotateRight(char currCardinalPos) {
        char rslt = currCardinalPos;
        switch (currCardinalPos) {
            case GlobalConstants.NORTH_POS:
                rslt = GlobalConstants.EAST_POS;
                break;
            case GlobalConstants.EAST_POS:
                rslt = GlobalConstants.SOUTH_POS;
                break;
            case GlobalConstants.SOUTH_POS:
                rslt = GlobalConstants.WEST_POS;
                break;
            case GlobalConstants.WEST_POS:
                rslt = GlobalConstants.NORTH_POS;
                break;
        }
        return rslt;
    }

    /**
     * get name
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieve the log trace for application
     *
     * @return List of strings
     */
    public List<String> retrieveTrace() {
        return traceList;
    }

    /**
     * Retrieve the log of robot positions used.
     *
     * @return List of RobotPosition
     */
    public List<RobotPosition> retrieveRobotPositionsLog() {
        return robotPosList;
    }

    /**
     * Log of actions performed.
     *
     * @return List of String result
     */
    public List<String> retrieveResultList() {
        return resultList;
    }
}
