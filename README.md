# FakeHealth - Samsung Health 클론 (시크릿 데이터 편집기)

실제 Samsung Health처럼 보이지만, **5번 빠르게 탭**하면 숨겨진 모드로 진입하여 걸음 수, 칼로리, 수면, 심박수 데이터를 임의로 조작할 수 있는 앱입니다.

## 주요 기능

### 🎭 공개 모드 (일반 사용자용)
- Samsung Health와 거의 동일한 UI/UX
- 걸음 수, 칼로리, 수면 시간, 심박수 표시
- 주간 통계 및 7일 차트
- 다크/라이트 모드 자동 지원

### 🔐 시크릿 모드 (숨겨진 기능)
- **메인 화면 아무 곳이나 5번 빠르게 탭** → PIN 입력 → 시크릿 모드 진입
- 최초 실행 시 4자리 PIN 설정
- 날짜 선택 후 원하는 수치로 자유롭게 변경 가능
- 저장 즉시 메인 대시보드에 반영

## 조작 가능한 데이터
| 항목 | 단위 | 설명 |
|------|------|------|
| 걸음 수 | 보 | 일일 걸음 수 |
| 칼로리 | kcal | 소모 칼로리 |
| 수면 | 시간/분 | 수면 시간 |
| 심박수 | bpm | 평균/최저/최고 심박수 |

## 빌드 방법

### 사전 요구사항
- JDK 17 이상
- Android SDK (API 35)
- Gradle 8.5+

### 명령어
```bash
# 프로젝트 루트에서
./gradlew assembleDebug

# 또는 Windows에서
gradlew.bat assembleDebug
```

### APK 위치
```
app/build/outputs/apk/debug/app-debug.apk
```

## 사용법

1. **앱 설치 및 실행** - Samsung Health 아이콘으로 표시됨
2. **첫 시크릿 진입** - 화면 아무 곳이나 5번 빠르게 탭 → PIN 설정 (4자리 숫자 2회 입력)
3. **이후 진입** - 5번 탭 → PIN 입력 → 시크릿 에디터 열림
4. **데이터 편집** - 날짜 선택 → 수치 입력 → "저장하기" 버튼
5. **확인** - 메인 화면에서 즉시 반영된 수치 확인

## 기술 스택
- **Language**: Kotlin
- **UI**: Jetpack Compose (Material 3)
- **Architecture**: MVVM + Repository
- **Database**: Room
- **Preferences**: DataStore (Preferences)
- **DI**: Manual (ViewModel + Application)

## 프로젝트 구조
```
app/src/main/java/com/fakehealth/
├── data/
│   ├── HealthData.kt       # 데이터 모델
│   ├── HealthDao.kt        # Room DAO
│   ├── AppDatabase.kt      # Room Database
│   ├── HealthRepository.kt # 데이터 레포지토리
│   └── SecretPrefs.kt      # PIN/시크릿 설정 관리
├── ui/
│   ├── theme/              # 테마 (Color, Type, Theme)
│   ├── components/         # 재사용 컴포넌트 (카드, 차트)
│   ├── screens/            # 화면 (Dashboard, SecretEditor, PinSetup, PinEntry)
│   ├── MainViewModel.kt    # 메인 ViewModel
│   └── MainActivity.kt     # 진입점
└── FakeHealthApplication.kt # Application 클래스
```

## 보안 참고사항
- PIN은 간단한 해시로 저장됨 (데모용)
- 프로덕션 사용 시 BCrypt/Argon2 등 강력한 해시 사용 권장
- DataStore 암호화 옵션 고려 필요

## 라이선스
개인 학습/테스트 용도로 자유롭게 사용하세요.