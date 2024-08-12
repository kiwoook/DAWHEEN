package com.study.dawheen.chat.repository;

import com.study.dawheen.chat.entity.ChatRoomUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomUserRepository extends JpaRepository<ChatRoomUser, Long>, ChatRoomUserRepositoryCustom {

    // 유저 아이디로 사용자의 챗 룸을 반환함

}
