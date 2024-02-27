package com.study.gyh.study;

import com.study.gyh.account.CurrentUser;
import com.study.gyh.domain.Account;
import com.study.gyh.study.form.StudyForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StudyController {

    @GetMapping("/new-Study")
    public String newStudForm(@CurrentUser Account account, Model model) {
        model.addAttribute("account", account);
        model.addAttribute(new StudyForm());
        return "study/form";
    }
}
