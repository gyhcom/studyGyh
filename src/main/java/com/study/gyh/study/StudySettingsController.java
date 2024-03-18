package com.study.gyh.study;

import com.study.gyh.account.CurrentUser;
import com.study.gyh.domain.Account;
import com.study.gyh.domain.Study;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/study/{path}/settings")
@RequiredArgsConstructor
public class StudySettingsController {

    private final StudyService studyService;

    @GetMapping("/description")
    public String viewStudySetting(@CurrentUser Account account, @PathVariable String path,
        Model model) {
        Study study = studyService.getStudyToUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(study);
        return "study/settings/description";
    }

    @GetMapping("/banner")
    public String studyImageForm(@CurrentUser Account account, @PathVariable String path,
        Model model) {
        Study study = studyService.getStudyToUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(study);
        return "study/settings/banner";
    }
}
