package it.tostao.simplespringmvc.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller for the homepage
 */
@Controller
public class Index {

    private static final Logger LOG = LoggerFactory.getLogger(Index.class);

    /**
     * Default constructor.
     */
    public Index() {

    }

    /**
     * Controller for the homepage
     * @return
     */
    @RequestMapping(value = { "/", "/index" }, method = RequestMethod.GET)
    public ModelAndView get() {
        LOG.info("Index#get() called");
        ModelAndView mav = new ModelAndView("index");
        return mav;
    }
}
