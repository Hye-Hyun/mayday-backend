# API 명세서 남은 상세페이지 본문

아래 내용은 Notion 플러그인이 다시 연결되면 남은 상세페이지에 그대로 붙여넣기 위한 본문입니다.

## 사용자 프로필 조회

대상 페이지: `사용자 프로필 조회`

```markdown
## 설명 {color="gray_bg"}
현재 로그인한 사용자의 프로필 정보를 조회합니다. 마이페이지, 설정 화면에서 사용자 이메일 표시 용도로 사용합니다.

## Header {color="gray_bg"}
```http
Authorization: Bearer {accessToken}
```

## Response {color="gray_bg"}
```json
{
  "status": 200,
  "message": "사용자 프로필 조회 성공",
  "data": {
    "userId": 1,
    "email": "njob@mayday.kr",
    "createdAt": "2026-08-01T12:00:00"
  }
}
```
```

## 업종군 및 초기 부수입 저장

대상 페이지: `업종군 및 초기 부수입 저장`

```markdown
## 설명 {color="gray_bg"}
초기 사용자 설정이 필요한 경우 업종군 또는 초기 부수입 정보를 저장합니다. 현재 기능명세 최신본에서는 필수 온보딩보다 기록 흐름이 우선이므로 후순위 API로 둡니다.

## Request Body {color="gray_bg"}
```json
{
  "businessType": "FREELANCER",
  "initialIncome": 0
}
```

## Response {color="gray_bg"}
```json
{
  "status": 200,
  "message": "온보딩 정보 저장 성공",
  "data": {
    "userId": 1,
    "businessType": "FREELANCER"
  }
}
```

## 비고 {color="gray_bg"}
업종군을 AI 분류 프롬프트에 활용할 경우에만 MVP에 포함합니다.
```

## 수입 기록 3.3% 공제 여부 및 총수입 환산값 계산

대상 페이지: `수입 기록 3.3% 공제 여부 및 총수입 환산값 계산`

```markdown
## 설명 {color="gray_bg"}
수입 기록 시 3.3% 원천징수 여부를 반영해 총수입 환산 금액을 미리 계산합니다. 공제 후 입금액이면 `입금액 ÷ 0.967`로 총수입을 계산합니다.

## Request Body {color="gray_bg"}
```json
{
  "receivedAmount": 967000,
  "withholdingApplied": true
}
```

## Response {color="gray_bg"}
```json
{
  "status": 200,
  "message": "수입 금액 계산 성공",
  "data": {
    "receivedAmount": 967000,
    "withholdingApplied": true,
    "grossIncomeAmount": 1000000
  }
}
```

## 비고 {color="gray_bg"}
공제 없이 입금된 경우 `grossIncomeAmount = receivedAmount`입니다.
```

## 수입 기록 상세 조회

대상 페이지: `수입 기록 상세 조회`

```markdown
## 설명 {color="gray_bg"}
수입 기록의 상세 정보를 조회합니다. 수입은 증빙 유형과 적격 여부가 `null`일 수 있으며, 3.3% 공제 여부와 총수입 환산 금액을 함께 내려줍니다.

## Path Parameter {color="gray_bg"}
`recordId`: 수입 기록 ID

## Response {color="gray_bg"}
```json
{
  "status": 200,
  "message": "수입 기록 상세 조회 성공",
  "data": {
    "recordId": 201,
    "type": "INCOME",
    "date": "2026-08-02",
    "merchantName": "크몽",
    "itemName": "디자인 용역",
    "amount": 967000,
    "grossIncomeAmount": 1000000,
    "withholdingApplied": true,
    "category": "SALES",
    "evidenceType": null,
    "qualifiedEvidence": null
  }
}
```
```

## 수입 기록 3.3% 공제 여부 수정

대상 페이지: `수입 기록 3.3% 공제 여부 수정`

```markdown
## 설명 {color="gray_bg"}
수입 기록의 3.3% 공제 여부 또는 수입 관련 필드를 수정합니다. 수정 후 장부와 홈/마이페이지 요약에 반영합니다.

## Path Parameter {color="gray_bg"}
`recordId`: 수입 기록 ID

## Request Body {color="gray_bg"}
```json
{
  "date": "2026-08-02",
  "merchantName": "크몽",
  "itemName": "디자인 용역",
  "receivedAmount": 967000,
  "withholdingApplied": true,
  "category": "SALES"
}
```

## Response {color="gray_bg"}
```json
{
  "status": 200,
  "message": "수입 기록 수정 성공",
  "data": {
    "recordId": 201,
    "grossIncomeAmount": 1000000
  }
}
```
```

## 경비·수입 항목 옵션 조회

대상 페이지: `경비·수입 항목 옵션 조회`

```markdown
## 설명 {color="gray_bg"}
프론트에서 계정과목 선택지와 표시명을 구성할 수 있도록 경비·수입 항목 enum을 조회합니다.

## Response {color="gray_bg"}
```json
{
  "status": 200,
  "message": "계정과목 옵션 조회 성공",
  "data": {
    "expenseCategories": [
      { "label": "제세공과금", "value": "TAXES_AND_DUES" },
      { "label": "임차료", "value": "RENT" },
      { "label": "기업업무추진비", "value": "BUSINESS_PROMOTION_EXPENSE" },
      { "label": "차량유지비", "value": "VEHICLE_MAINTENANCE" },
      { "label": "지급수수료", "value": "SERVICE_FEES" },
      { "label": "소모품비", "value": "SUPPLIES" },
      { "label": "운반비", "value": "DELIVERY_EXPENSE" },
      { "label": "광고선전비", "value": "ADVERTISING_EXPENSE" },
      { "label": "여비교통비", "value": "TRAVEL_AND_TRANSPORTATION" },
      { "label": "기타(비용)", "value": "OTHER_EXPENSE" }
    ],
    "incomeCategories": [
      { "label": "매출", "value": "SALES" },
      { "label": "기타(수입)", "value": "OTHER_INCOME" }
    ]
  }
}
```
```

## AI 분석 결과 신뢰도 뱃지 기준 조회

대상 페이지: `AI 분석 결과 신뢰도 뱃지 기준 조회`

```markdown
## 설명 {color="gray_bg"}
AI 분석 신뢰도 뱃지의 기준값을 조회합니다. 후순위 기능이며, 프론트가 고정 상수로 처리해도 됩니다.

## Response {color="gray_bg"}
```json
{
  "status": 200,
  "message": "AI 신뢰도 기준 조회 성공",
  "data": [
    { "label": "안심", "color": "GREEN", "minScore": 95, "maxScore": 100 },
    { "label": "주의", "color": "YELLOW", "minScore": 70, "maxScore": 94 },
    { "label": "위험", "color": "RED", "minScore": 0, "maxScore": 69 }
  ]
}
```

## 비고 {color="gray_bg"}
후순위 기능이므로 MVP에서는 응답에 `confidenceScore`만 포함하고 뱃지 기준은 프론트 상수로 둘 수 있습니다.
```
