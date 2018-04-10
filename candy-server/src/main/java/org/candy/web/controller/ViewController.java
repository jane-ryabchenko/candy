package org.candy.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * Candy comparison controller.
 */
@RestController
public class ViewController {
  @GetMapping(path = "/{reportId}")
  public ModelAndView getPage(@PathVariable("reportId") String reportId, ModelAndView mv) {
    mv.addObject("reportId", reportId);
    mv.setViewName("index");
    return mv;
  }
}
