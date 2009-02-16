package org.globus.dispatch.camel;

import org.apache.camel.builder.RouteBuilder;

/**
 * Created by IntelliJ IDEA.
 * User: turtlebender
 * Date: Feb 13, 2009
 * Time: 10:04:36 AM
 * To change this template use File | Settings | File Templates.
 */
public class CamelRouteBuilder extends RouteBuilder {
    private String startAddress;
    private String targetAddress;
    private ResourcefulMessageEnhancer enhancer;

    public ResourcefulMessageEnhancer getEnhancer() {
        return enhancer;
    }

    public void setEnhancer(ResourcefulMessageEnhancer enhancer) {
        this.enhancer = enhancer;
    }

    public String getTargetAddress() {
        return targetAddress;
    }

    public void setTargetAddress(String targetAddress) {
        this.targetAddress = targetAddress;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public void configure() throws Exception {
        from(startAddress).process(enhancer).to(targetAddress);
    }
}
