package com.study.gyh.settings;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.study.gyh.WithAccount;
import com.study.gyh.account.AccountRepository;
import com.study.gyh.domain.Account;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class SettingsControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountRepository accountRepository;

    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();

    }

    @WithAccount("gyh")
    @DisplayName("프로필 수정하기 폼")
    @Test
    void updateProfileForm() throws Exception {
        mockMvc.perform(get(SettingsController.SETTINGS_PROFILE_URL))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("account"))
            .andExpect(model().attributeExists("profile"));

    }

    @WithAccount("gyh")
    @DisplayName("프로필 수정하기 - 입력값 정상")
    @Test
    void updateProfile() throws Exception {
        String bio = "짧은 소개 수정";
        mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_URL)
                .param("bio", bio)
                .with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl(SettingsController.SETTINGS_PROFILE_URL))
            .andExpect(flash().attributeExists("message"));

        Account gyh = accountRepository.findByNickname("gyh");
        System.out.println(gyh.toString());

        assertEquals(bio, gyh.getBio());
    }

    @WithAccount("gyh")
    @DisplayName("프로필 수정하기 - 입력값 에러")
    @Test
    void updateProfile_error() throws Exception {
        String bio = "길게 소개를 수정하는 경우, 길게 길게 소개를 수정하는 경우, 길게 길게 소개를 수정하는 경우, 길게 길게 소개를 수정하는 경우, 길게";
        mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_URL)
                .param("bio", bio)
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(view().name(SettingsController.SETTINGS_PROFILE_VIEW_NAME))
            .andExpect(model().attributeExists("account"))
            .andExpect(model().attributeExists("profile"))
            .andExpect(model().hasErrors());

        Account gyh = accountRepository.findByNickname("gyh");
        System.out.println(gyh.toString());

        assertNull(gyh.getBio());
    }

    @WithAccount("gyh")
    @DisplayName("패스워드 수정 폼")
    @Test
    void updatePassWord_Form() throws Exception {
        mockMvc.perform(get(SettingsController.SETTINGS_PASSWORD_URL))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("account"))
            .andExpect(model().attributeExists("passwordForm"));
    }
}