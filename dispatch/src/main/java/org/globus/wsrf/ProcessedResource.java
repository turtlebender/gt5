package org.globus.wsrf;

import org.globus.wsrf.properties.Resource;

public class ProcessedResource {
    Resource resource;
    XPathEvaluator evaluator;

    public ProcessedResource() {
    }

    public ProcessedResource(Resource resource, XPathEvaluator evaluator) {
        this.resource = resource;
        this.evaluator = evaluator;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public XPathEvaluator getEvaluator() {
        return evaluator;
    }

    public void setEvaluator(XPathEvaluator evaluator) {
        this.evaluator = evaluator;
    }
}
