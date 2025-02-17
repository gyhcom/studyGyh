/* (C)2024 */
package com.study.gyh.tag;

import com.study.gyh.domain.Tag;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface TagRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findById(String title);

    Tag findByTitle(String title);
}
