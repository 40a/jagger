package com.griddynamics.jagger.xml.beanParsers.configuration;

import com.griddynamics.jagger.user.ProcessingConfig;
import com.griddynamics.jagger.xml.beanParsers.CustomBeanDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.XMLConstants;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;
import java.util.List;


public class TaskDefinitionParser extends CustomBeanDefinitionParser {

    @Override
    protected Class getBeanClass(Element element) {
        return ProcessingConfig.Test.Task.class;
    }

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        super.doParse(element, parserContext, builder);

        if (!DomUtils.getChildElementsByTagName(element, XMLConstants.USER).isEmpty()) {
            setBeanListProperty(XMLConstants.USERS, false, element, parserContext, builder);
        } else {
            if(DomUtils.getChildElementByTagName(element, XMLConstants.TPS) != null){
                setBeanProperty(XMLConstants.TPS, DomUtils.getChildElementByTagName(element, XMLConstants.TPS), parserContext, builder);
            }else{
                if(DomUtils.getChildElementByTagName(element, XMLConstants.VIRTUAL_USER) != null){
                    setBeanProperty(XMLConstants.VIRTUAL_USER_CLASS_FIELD, DomUtils.getChildElementByTagName(element, XMLConstants.VIRTUAL_USER), parserContext, builder);
                }else{
                    setBeanProperty(XMLConstants.INVOCATION, DomUtils.getChildElementByTagName(element, XMLConstants.INVOCATION), parserContext, builder);
                }
            }
        }
    }
}
