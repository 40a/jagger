package com.griddynamics.jagger.xml.beanParsers;

import com.griddynamics.jagger.master.configuration.Configuration;
import com.griddynamics.jagger.master.configuration.UserTaskGenerator;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 12/10/12
 * Time: 11:21 AM
 * To change this template use File | Settings | File Templates.
 */
public class ConfigDefinitionParser extends AbstractSimpleBeanDefinitionParser {


    @Override
    protected Class getBeanClass(Element element) {
        return Configuration.class;
    }

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {

        //parse session-listeners
        Element sListenerGroup = DomUtils.getChildElementByTagName(element, XMLConstants.SESSION_EXECUTION_LISTENERS);
        ManagedList slList = new ManagedList();

        //add standard listeners
        for (String sessionListener : XMLConstants.STANDARD_SESSION_EXEC_LISTENERS){
            slList.add(new RuntimeBeanReference(sessionListener));
        }

        //add user's listeners
        if (sListenerGroup != null){
            if (!sListenerGroup.getAttribute(XMLConstants.ATTRIBUTE_REF).isEmpty()){
                builder.addPropertyReference(XMLConstants.SESSION_EXECUTION_LISTENERS_CLASS_FIELD, sListenerGroup.getAttribute(XMLConstants.ATTRIBUTE_REF));
            }else{
                List<Element> sl = DomUtils.getChildElements(sListenerGroup);

                for (Element el : sl){
                    if (!el.getAttribute(XMLConstants.ATTRIBUTE_REF).isEmpty()){
                        slList.add(new RuntimeBeanReference(el.getAttribute(XMLConstants.ATTRIBUTE_REF)));
                    }else{
                        slList.add(parserContext.getDelegate().parsePropertySubElement(el, builder.getBeanDefinition()));
                    }
                }
            }
        }

        builder.addPropertyValue(XMLConstants.SESSION_EXECUTION_LISTENERS_CLASS_FIELD, slList);

        //Parse task-listeners
        Element tListenerGroup = DomUtils.getChildElementByTagName(element, XMLConstants.TASK_EXECUTION_LISTENERS);
        ManagedList tlList = new ManagedList();

        //add standard listeners
        for (String sessionListener : XMLConstants.STANDARD_TASK_EXEC_LISTENERS){
            tlList.add(new RuntimeBeanReference(sessionListener));
        }

        //add user's listeners
        if (tListenerGroup != null){
            if (!sListenerGroup.getAttribute(XMLConstants.ATTRIBUTE_REF).isEmpty()){
                builder.addPropertyReference(XMLConstants.TASK_EXECUTION_LISTENERS_CLASS_FIELD, sListenerGroup.getAttribute(XMLConstants.ATTRIBUTE_REF));
            }else{
                List<Element> tl = DomUtils.getChildElements(tListenerGroup);

                for (Element el : tl){
                    if (!el.getAttribute(XMLConstants.ATTRIBUTE_REF).isEmpty()){
                        tlList.add(new RuntimeBeanReference(el.getAttribute(XMLConstants.ATTRIBUTE_REF)));
                    }else{
                        tlList.add(parserContext.getDelegate().parsePropertySubElement(el, builder.getBeanDefinition()));
                    }
                }
            }
        }

        builder.addPropertyValue(XMLConstants.TASK_EXECUTION_LISTENERS_CLASS_FIELD, tlList);


        //parse test-plan
        Element testPlan = DomUtils.getChildElementByTagName(element, XMLConstants.TEST_PLAN);

        BeanDefinitionBuilder generator = BeanDefinitionBuilder.genericBeanDefinition(UserTaskGenerator.class);
        parserContext.getRegistry().registerBeanDefinition(XMLConstants.GENERATOR, generator.getBeanDefinition());

        if (!element.getAttribute(XMLConstants.MONITORING_ENABLE).isEmpty()){
            generator.addPropertyValue(XMLConstants.MONITORING_ENABLE, element.getAttribute(XMLConstants.MONITORING_ENABLE));
        }

        if (!testPlan.getAttribute(XMLConstants.ATTRIBUTE_REF).isEmpty()){
            generator.addPropertyReference(XMLConstants.CONFIG, testPlan.getAttribute(XMLConstants.ATTRIBUTE_REF));
        }else{
            generator.addPropertyValue(XMLConstants.CONFIG, parserContext.getDelegate().parseCustomElement(testPlan, builder.getBeanDefinition()));
        }
        builder.addPropertyValue(XMLConstants.TASKS, XMLConstants.GENERATOR_GENERATE);
    }
}
