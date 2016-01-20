package com.lestarch.autoclick;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.util.Random;

/**
 * A robot designed to automatically click at the specified interval
 * 
 * @author starchmd
 */
public class ClickingRobot implements Runnable {
    //Constants
    private static final int REACTION_INTERVAL = 20;
    private static final float JITTER_MAX = 0.10f;
    
    //Internal variables
    private int interval = 300;
    private boolean jitter = false;
    private Point location = new Point(0,0);

    private Robot robot;
    private Random rand = new Random();
    /**
     * Clicking robot constructor
     */
    public ClickingRobot() throws AWTException {
        this.robot = new Robot();
    }
    
    @Override
    public void run() {
        while (true) {
            this.robot.mouseMove(this.applyJitter(this.location.x), this.applyJitter(this.location.y));
            this.safeSleep(this.applyJitter(REACTION_INTERVAL));  
            this.robot.mousePress(InputEvent.BUTTON1_MASK);
            this.safeSleep(this.applyJitter(REACTION_INTERVAL));
            this.robot.mouseRelease(InputEvent.BUTTON1_MASK);
            this.safeSleep(this.applyJitter(REACTION_INTERVAL));
            this.safeSleep(this.applyJitter(interval));
        }
    }
    /**
     * Get the current clicking interval
     * @return interval
     */
    public int getInterval() {
        return this.interval;
    }
    /**
     * Get the current jitter
     * @return jitter
     */
    public boolean getJitter() {
        return this.jitter;
    }
    /**
     * Sets the interval to click
     */
    public void setInterval(int interval) {
        this.interval = interval;
    }
    /**
     * Set the jitter
     * @param jitter - jitter the mouse, true/false 
     */
    public void setJitter(boolean jitter) {
        this.jitter = jitter;
    }
    /**
     * Set the mouse click location
     * @param location - location
     */
    public void setLocation(Point location) {
        this.location = location;
    }
    /**
     * Sleep safely for given number of mili seconds
     * @param ms - miliseconds
     */
    private void safeSleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    /**
     * Applies jitter (up to certain variance) to given
     * return randomized integer
     */
    private int applyJitter(int value) {
        if (!this.jitter)
            return value;
        float percentage = (JITTER_MAX*2.0f*rand.nextFloat()) - JITTER_MAX;
        return Math.round((float)value * (1.0f+percentage));
    }
}
