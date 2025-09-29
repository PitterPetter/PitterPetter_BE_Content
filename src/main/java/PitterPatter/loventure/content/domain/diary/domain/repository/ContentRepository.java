package PitterPatter.loventure.content.domain.diary.domain.repository;

import PitterPatter.loventure.content.domain.diary.domain.entity.Content;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ContentRepository extends JpaRepository<Content, Long> {
}
