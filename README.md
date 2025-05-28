# Currency Exchanger App

An Android app built with Kotlin that allows users to convert currencies using real-time exchange
rates, track balances of different currencies, and apply flexible commission rules.

---

## Features

- **Currency Conversion**: Convert between supported currencies with up-to-date exchange rates.
- **Balances**: Initial balance of 1000 EUR. Balances update after each transaction and are
  persisted locally.
- **Commission Rules**:
    - First 5 conversions are **free**.
    - Every 10th conversion is **free**.
    - Conversions **≤ 150 EUR** are **free**.
    - All others include a **0.7% commission**.
- **Live Rate Preview**: See the converted amount instantly as you type.
- **Balance Display**: User balances are clearly shown in the UI.
- **Expandable System**: Easily add new currencies or update commission rules.
- **Error Handling**:  Done by handling API and conversion issues with user-friendly messages.
- **Separation of Concerns**: MVVM architecture, Repository pattern, clean ViewModel logic.

---

## Tech Stack

- **Language**: Kotlin
- **Architecture**: MVVM
- **UI**: XML Layouts + ViewBinding
- **Data Flow**: Kotlin Coroutines & Flow
- **Persistence**: SharedPreferences
- **Network**: Retrofit
- **List Handling**: RecyclerView + ListAdapter

---

## Testing

- Core features tested manually
- UI tested for edge cases (e.g. 0 input, invalid currency pairs)
- App handles loss of network gracefully

---

## How to Extend

- **Add New Currencies**: Update the rates API and the balance manager.
- **Change Commission Logic**: Modify `CommissionAmountPolicy` or inject a new implementation.
- **Add Analytics / History**: Save conversion logs and show them in a RecyclerView.

---

## License

This project is for educational/assessment purposes only.

