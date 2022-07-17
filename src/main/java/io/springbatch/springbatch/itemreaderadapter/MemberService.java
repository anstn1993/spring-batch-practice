package io.springbatch.springbatch.itemreaderadapter;

import io.springbatch.springbatch.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
  private final MemberRepository memberRepository;
  private int id = 1;

  public Member getMember() {
    return memberRepository.findById(id++).orElse(null);
  }
}
