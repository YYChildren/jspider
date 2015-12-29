package net.javacoding.jspider.core.impl;

import net.javacoding.jspider.core.logging.Log;
import net.javacoding.jspider.core.logging.LogFactory;
import net.javacoding.jspider.core.util.config.PropertySet;
import net.javacoding.jspider.spi.Plugin;
import net.javacoding.jspider.mod.plugin.console.ConsolePlugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * $Id: PluginInstantiator.java,v 1.3 2003/04/03 16:24:51 vanrogu Exp $
 */
public class PluginInstantiator {

    protected Log log;

    public PluginInstantiator() {
        log = LogFactory.getLog(PluginInstantiator.class);
    }


    public Plugin instantiate(Class<?> pluginClass, String name, PropertySet config) {
        Plugin plugin = null;

        try {
            log.debug("first trying to instantiate via ctr with (name, config) params");
            plugin = instantiateWithNameAndConfig(pluginClass, name, config);

            if (plugin == null) {
                log.debug("plugin not yet instantiated, trying via ctr with (config) param");
                plugin = instantiateWithConfig(pluginClass, name, config);
            }
            if (plugin == null) {
                log.debug("plugin not yet instantiated, trying via ctr with (name) param");
                plugin = instantiateWithName(pluginClass, name, config);
            }
            if (plugin == null) {
                log.debug("plugin not yet instantiated, trying via default constructor");
                plugin = (Plugin) pluginClass.newInstance();
            }
        } catch (InstantiationException e) {
            log.error("cannot instantiate module - defaulting to console", e);
            plugin = new ConsolePlugin();
        } catch (IllegalAccessException e) {
            log.error("cannot instantiate module - defaulting to console", e);
            plugin = new ConsolePlugin();
        }
        log.debug("plugin instantiated.");
        return plugin;
    }

	protected Plugin instantiateWithNameAndConfig(Class<?> pluginClass, String name, PropertySet config) {
        Plugin plugin = null;
        try {
            Constructor<?> constructor = pluginClass.getDeclaredConstructor(String.class,PropertySet.class);
            plugin = (Plugin) constructor.newInstance(name,config);
        } catch (NoSuchMethodException e) {
            log.debug("cannot instantiate module - constructor with name and PropertySet params not found", e);
        } catch (InstantiationException e) {
            log.debug("cannot instantiate module - InstantiationException", e);
        } catch (InvocationTargetException e) {
            log.debug("cannot instantiate module - InvocationTargetException", e);
        } catch (IllegalAccessException e) {
            log.debug("cannot instantiate module - IllegalAccessException", e);
        }
        return plugin;
    }

    protected Plugin instantiateWithConfig(Class<?> pluginClass, String name, PropertySet config) {
        Plugin plugin = null;
        try {
            Constructor<?> constructor = pluginClass.getDeclaredConstructor(PropertySet.class);
            plugin = (Plugin) constructor.newInstance(config);
        } catch (NoSuchMethodException e) {
            log.debug("cannot instantiate module - constructor with PropertySet param not found", e);
        } catch (InstantiationException e) {
            log.debug("cannot instantiate module - InstantiationException", e);
        } catch (InvocationTargetException e) {
            log.debug("cannot instantiate module - InvocationTargetException", e);
        } catch (IllegalAccessException e) {
            log.debug("cannot instantiate module - IllegalAccessException", e);
        }
        return plugin;
    }

    protected Plugin instantiateWithName(Class<?> pluginClass, String name, PropertySet config) {
        Plugin plugin = null;
        try {
            Constructor<?> constructor = pluginClass.getDeclaredConstructor(String.class);
            plugin = (Plugin) constructor.newInstance(name);
        } catch (NoSuchMethodException e) {
            log.debug("cannot instantiate module - constructor with name param not found", e);
        } catch (InstantiationException e) {
            log.debug("cannot instantiate module - InstantiationException", e);
        } catch (InvocationTargetException e) {
            log.debug("cannot instantiate module - InvocationTargetException", e);
        } catch (IllegalAccessException e) {
            log.debug("cannot instantiate module - IllegalAccessException", e);
        }
        return plugin;
    }

}
