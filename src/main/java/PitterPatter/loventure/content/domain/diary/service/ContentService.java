package PitterPatter.loventure.content.domain.diary.service;

import PitterPatter.loventure.content.domain.diary.domain.entity.Diary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContentService {

    public Diary saveDiary(Diary diary) {
        // TODO: 실제 Repository를 주입받아서 저장 로직 구현
        // 현재는 임시로 전달받은 diary를 그대로 반환
        return diary;
    }
}
