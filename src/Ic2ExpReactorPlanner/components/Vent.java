/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Ic2ExpReactorPlanner.components;

import Ic2ExpReactorPlanner.MaterialsList;
import Ic2ExpReactorPlanner.ReactorComponent;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents some kind of vent in a reactor.
 * @author Brian McCloud
 */
public class Vent extends ReactorItem {
    
    private final int selfVent;
    private final int hullDraw;
    private final int sideVent;
    
    public Vent(final int id, final String baseName, final String name, final String imageFilename, final double maxDamage, final double maxHeat, final String sourceMod, final MaterialsList materials,
            final int selfVent, final int hullDraw, final int sideVent) {
        super(id, baseName, name, imageFilename, maxDamage, maxHeat, sourceMod, materials);
        this.selfVent = selfVent;
        this.hullDraw = hullDraw;
        this.sideVent = sideVent;
    }
    
    public Vent(final Vent other) {
        super(other);
        this.selfVent = other.selfVent;
        this.hullDraw = other.hullDraw;
        this.sideVent = other.sideVent;
    }
    
    @Override
    public double dissipate() {
        double deltaHeat = Math.min(hullDraw, parent.getCurrentHeat());
        parent.adjustCurrentHeat(-deltaHeat);
        this.adjustCurrentHeat(deltaHeat);
        final double currentDissipation = Math.min(selfVent, getCurrentHeat());
        currentVentCooling = currentDissipation;
        parent.ventHeat(currentDissipation);
        adjustCurrentHeat(-currentDissipation);
        if (sideVent > 0) {
            List<ReactorComponent> coolableNeighbors = new ArrayList<>(4);
            ReactorComponent component = parent.getComponentAt(row - 1, col);
            if (component != null && component.isHeatAcceptor()) {
                coolableNeighbors.add(component);
            }
            component = parent.getComponentAt(row, col + 1);
            if (component != null && component.isHeatAcceptor()) {
                coolableNeighbors.add(component);
            }
            component = parent.getComponentAt(row + 1, col);
            if (component != null && component.isHeatAcceptor()) {
                coolableNeighbors.add(component);
            }
            component = parent.getComponentAt(row, col - 1);
            if (component != null && component.isHeatAcceptor()) {
                coolableNeighbors.add(component);
            }
            for (ReactorComponent coolableNeighbor : coolableNeighbors) {
                double rejectedCooling = coolableNeighbor.adjustCurrentHeat(-sideVent);
                double tempDissipatedHeat = sideVent + rejectedCooling;
                parent.ventHeat(tempDissipatedHeat);
                currentVentCooling += tempDissipatedHeat;
            }
        }
        bestVentCooling = Math.max(bestVentCooling, currentVentCooling);
        return currentDissipation;
    }
    
    @Override
    public double getVentCoolingCapacity() {
        double result = selfVent;
        if (sideVent > 0) {
            ReactorComponent component = parent.getComponentAt(row - 1, col);
            if (component != null && component.isHeatAcceptor()) {
                result += sideVent;
            }
            component = parent.getComponentAt(row, col + 1);
            if (component != null && component.isHeatAcceptor()) {
                result += sideVent;
            }
            component = parent.getComponentAt(row + 1, col);
            if (component != null && component.isHeatAcceptor()) {
                result += sideVent;
            }
            component = parent.getComponentAt(row, col - 1);
            if (component != null && component.isHeatAcceptor()) {
                result += sideVent;
            }
        }
        return result;
    }
    
 }