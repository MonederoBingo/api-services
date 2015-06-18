package com.neerpoints.migrations.util;

import com.github.slugify.Slugify;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * Represents an Ant task to generate a slug from a given string
 */
public class SlugifyTask extends Task {
    private String _string;
    private String _propertyName;
    private Slugify _slugify;

    /**
     * Establish the String source value
     *
     * @param string The String value
     */
    public void setString(String string) {
        _string = string;
    }

    /**
     * Establish the property name to used in the ant build.xml file
     *
     * @param propertyName The property name value
     */
    public void setPropertyName(String propertyName) {
        _propertyName = propertyName;
    }

    @Override
    public void init() throws BuildException {
        _slugify = new Slugify();
    }

    @Override
    public void execute() throws BuildException {
        getProject().setProperty(_propertyName, _slugify.slugify(_string));
    }
}
