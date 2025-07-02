package pvp;

//선공 기회는 랜덤, 턴제 게임, 공격 시 최대 50의 데미지를 입힐 수 있다
//damage가 강한 스킬일 수록 miss 발생 확률이 커진다, 스킬은 총 3가지(캐릭터 다양성은 나중에 추가)
//적의 공격 방식은 랜덤
class Skill {
    String name;
    int damage;
    int accuracy;

    Skill(String name, int damage, int accuracy) {
        this.name = name;
        this.damage = damage;
        this.accuracy = accuracy;
    }

    boolean hit() {
        return new java.util.Random().nextInt(100) < accuracy;
    }
}
