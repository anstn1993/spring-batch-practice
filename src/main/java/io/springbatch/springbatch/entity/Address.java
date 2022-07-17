package io.springbatch.springbatch.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(exclude = "member")
@Entity(name = "address")
public class Address {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  private String location;

  @OneToOne
  @JoinColumn(name = "member_id")
  private Member member;
}
