package com.capstone.domain.journal.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record JournalCreateRequestDto(

    @Size(max = 255, message = "제목은 255자 이하로 입력해주세요.")
    String title,

    @NotBlank(message = "저널 내용은 필수입니다.")
    String content,

    @NotNull(message = "저널 작성 날짜는 필수입니다.")
    LocalDate journalDate
) {
}