/* (C)2024 */
package com.study.gyh.settings;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.gyh.WithAccount;
import com.study.gyh.account.AccountRepository;
import com.study.gyh.account.AccountService;
import com.study.gyh.domain.Account;
import com.study.gyh.domain.Tag;
import com.study.gyh.settings.form.TagForm;
import com.study.gyh.tag.TagRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
class SettingsControllerTest {

    @Autowired MockMvc mockMvc;

    @Autowired AccountRepository accountRepository;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired ObjectMapper objectMapper;
    @Autowired TagRepository tagRepository;
    @Autowired AccountService accountService;

    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
    }

    @WithAccount("gyh")
    @DisplayName("계정의 태그 수정 폼")
    @Test
    void updateTagsForm() throws Exception {
        mockMvc.perform(get(SettingsController.SETTINGS_TAGS_URL))
                .andExpect(view().name(SettingsController.SETTINGS_TAGS_VIEW_NAME))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("whitelist"))
                .andExpect(model().attributeExists("tags"));
    }

    @Transactional
    @WithAccount("gyh")
    @DisplayName("계정에 태그 추가")
    @Test
    void addTag() throws Exception {

        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("newTag");

        mockMvc.perform(
                        post(SettingsController.SETTINGS_TAGS_URL + "/add")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(tagForm))
                                .with(csrf()))
                .andExpect(status().isOk());

        Tag newTag = tagRepository.findByTitle("newTag");
        assertNotNull(newTag);
        Account gyh = accountRepository.findByNickname("gyh");
        assertTrue(gyh.getTags().contains(newTag));
    }

    @Transactional
    @WithAccount("gyh")
    @DisplayName("계정에 태그 삭제")
    @Test
    void removeTag() throws Exception {
        // TODO: 테스트 실패 디버거 해봐라 ^_^ 그래야 실력이 늘지
        Account gyh = accountRepository.findByNickname("gyh");
        Tag newTag = tagRepository.save(Tag.builder().title("newTag").build());
        accountService.addTag(gyh, newTag);

        assertTrue(gyh.getTags().contains(newTag));

        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("newTag");

        mockMvc.perform(
                        post(SettingsController.SETTINGS_TAGS_URL + "/remove")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(tagForm))
                                .with(csrf()))
                .andExpect(status().isOk());

        assertFalse(gyh.getTags().contains(newTag));
    }

    @WithAccount("gyh")
    @DisplayName("닉네임 수정하기 - 입력값 정상")
    @Test
    void updateAccount_success() throws Exception {
        String newNickname = "gyhcom";
        mockMvc.perform(
                        post(SettingsController.SETTINGS_ACCOUNT_URL)
                                .param("nickname", newNickname)
                                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.SETTINGS_ACCOUNT_URL))
                .andExpect(flash().attributeExists("message"));
    }

    @WithAccount("gyh")
    @DisplayName("닉네임 수정하기 - 입력값 정상")
    @Test
    void updateAccount_failure() throws Exception {
        String newNickname = "\\_()_/+;";
        mockMvc.perform(
                        post(SettingsController.SETTINGS_ACCOUNT_URL)
                                .param("nickname", newNickname)
                                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTINGS_ACCOUNT_VIEW_NAME))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("nicknameForm"));
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
        mockMvc.perform(
                        post(SettingsController.SETTINGS_PROFILE_URL)
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
        String bio = "길게 소개를 수정하는 경우, 길게 길게 소개를 수정하는 경우, 길게 길게 소개를 수정하는 경우, 길게 소개를 수정하는 경우, 길게";
        mockMvc.perform(
                        post(SettingsController.SETTINGS_PROFILE_URL)
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

    @WithAccount("gyh")
    @DisplayName("패스워드 수정 - 입력값 정상")
    @Test
    void updatePassWord_success() throws Exception {
        mockMvc.perform(
                        post(SettingsController.SETTINGS_PASSWORD_URL)
                                .param("newPassword", "12345678")
                                .param("newPasswordConfirm", "12345678")
                                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.SETTINGS_PASSWORD_URL))
                .andExpect(flash().attributeExists("message"));

        Account gyh = accountRepository.findByNickname("gyh");
        assertTrue(passwordEncoder.matches("12345678", gyh.getPassword()));
    }

    @WithAccount("gyh")
    @DisplayName("패스워드 수정 - 입력값 에러")
    @Test
    void updatePassword_fail() throws Exception {
        mockMvc.perform(
                        post(SettingsController.SETTINGS_PASSWORD_URL)
                                .param("newPassword", "12345678")
                                .param("newPasswordConfirm", "11111111")
                                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTINGS_PASSWORD_VIEW_NAME))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("passwordForm"))
                .andExpect(model().attributeExists("account"));
    }
}
