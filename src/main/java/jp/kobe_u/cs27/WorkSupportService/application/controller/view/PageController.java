package jp.kobe_u.cs27.WorkSupportService.application.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;

/**
 * WSService全体のコントローラー
 *
 * @author ing
 */
@Controller
@RequiredArgsConstructor
public class PageController {
    /**
     * 一番上位の層、index.htmlへ渡す
     *
     * @return index
     */
    @GetMapping("/")
    public String showLandingPage() {
        return "index";
    }
}
