package com.study.dawheen.chat.repository.impl;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.dawheen.chat.dto.ChatRoomInfoResponseDto;
import com.study.dawheen.chat.entity.QChatRoom;
import com.study.dawheen.chat.entity.QChatRoomUser;
import com.study.dawheen.chat.repository.ChatRoomUserRepositoryCustom;
import com.study.dawheen.user.entity.QUser;
import com.study.dawheen.volunteer.entity.QVolunteerWork;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ChatRoomUserRepositoryCustomImpl implements ChatRoomUserRepositoryCustom {

    private static final QChatRoom chatRoom = QChatRoom.chatRoom;
    private static final QChatRoomUser chatRoomUser = QChatRoomUser.chatRoomUser;
    private static final QUser user = QUser.user;
    private static final QVolunteerWork volunteerWork = QVolunteerWork.volunteerWork;

    private final JPAQueryFactory queryFactory;

    @Autowired
    public ChatRoomUserRepositoryCustomImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }


    @Override
    public List<ChatRoomInfoResponseDto> findChatRoomByUserId(String userId) {


        return queryFactory
                .select(Projections.constructor(
                        ChatRoomInfoResponseDto.class,
                        chatRoom
                ))
                .from(chatRoom)
                .join(chatRoom.volunteerWork, volunteerWork).fetchJoin()
                .where(chatRoom.id.in(
                        JPAExpressions.select(chatRoomUser.chatRoom.id)
                                .from(chatRoomUser)
                                .join(chatRoomUser.user, user)
                                .where(chatRoomUser.user.email.eq(userId))
                ))
                .fetch();
    }
}
