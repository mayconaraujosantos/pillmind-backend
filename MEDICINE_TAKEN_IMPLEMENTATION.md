# 💊 MedicineTaken System - Implementation Plan

## 1. Backend Implementation

### Domain Entity
```java
// src/main/java/com/pillmind/domain/models/MedicineTaken.java
package com.pillmind.domain.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record MedicineTaken(
        String id,
        String userId,
        String medicineId,
        LocalDate date,
        String scheduledTime, // "08:00", "14:00", "20:00"
        LocalDateTime takenAt,
        boolean skipped,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) implements Entity {
}
```

### Database Schema
```sql
CREATE TABLE medicine_taken (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    medicine_id VARCHAR(36) NOT NULL,
    date DATE NOT NULL,
    scheduled_time VARCHAR(8) NOT NULL,
    taken_at TIMESTAMP NULL,
    skipped BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    
    FOREIGN KEY (medicine_id) REFERENCES medicines(id) ON DELETE CASCADE,
    UNIQUE KEY unique_dose (medicine_id, date, scheduled_time)
);
```

### Repository Interface
```java
// src/main/java/com/pillmind/data/protocols/db/MedicineTakenRepository.java
public interface MedicineTakenRepository {
    List<MedicineTaken> findByMedicineAndDateRange(String medicineId, LocalDate from, LocalDate to);
    List<MedicineTaken> findByUserAndDate(String userId, LocalDate date);
    Optional<MedicineTaken> findByMedicineAndDateAndTime(String medicineId, LocalDate date, String time);
    MedicineTaken insert(MedicineTaken taken);
    void update(MedicineTaken taken);
    boolean deleteById(String id);
}
```

### Use Cases
```java
// src/main/java/com/pillmind/domain/usecases/MarkMedicineAsTaken.java
public interface MarkMedicineAsTaken extends UseCase<MarkMedicineAsTaken.Params, MedicineTaken> {
    record Params(
        String userId,
        String medicineId, 
        LocalDate date,
        String scheduledTime,
        LocalDateTime takenAt
    ) {}
}

// src/main/java/com/pillmind/domain/usecases/SkipMedicineDose.java
public interface SkipMedicineDose extends UseCase<SkipMedicineDose.Params, MedicineTaken> {
    record Params(
        String userId,
        String medicineId,
        LocalDate date, 
        String scheduledTime
    ) {}
}

// src/main/java/com/pillmind/domain/usecases/GetMedicineTakesForDay.java 
public interface GetMedicineTakesForDay extends UseCase<GetMedicineTakesForDay.Params, List<MedicineTaken>> {
    record Params(String userId, LocalDate date) {}
}
```

### HTTP Controller
```java
// src/main/java/com/pillmind/presentation/controllers/MedicineTakenController.java
@RestController
public class MedicineTakenController {
    
    @PostMapping("/api/medicines/{medicineId}/doses/take")
    public ResponseEntity<MedicineTakenResponse> markAsTaken(
        @PathVariable String medicineId,
        @RequestBody MarkAsTakenRequest request,
        HttpServletRequest httpRequest
    ) {
        String userId = resolveUserId(httpRequest);
        // ...
    }
    
    @PostMapping("/api/medicines/{medicineId}/doses/skip")
    public ResponseEntity<MedicineTakenResponse> skipDose(
        @PathVariable String medicineId, 
        @RequestBody SkipDoseRequest request,
        HttpServletRequest httpRequest
    ) {
        // ...
    }
    
    @GetMapping("/api/medicines/{medicineId}/doses")
    public ResponseEntity<List<MedicineTakenResponse>> getDoses(
        @PathVariable String medicineId,
        @RequestParam String date,
        HttpServletRequest httpRequest
    ) {
        // ...
    }
    
    @GetMapping("/api/medicines/doses/today")
    public ResponseEntity<List<MedicineTakenResponse>> getTodayDoses(
        HttpServletRequest httpRequest
    ) {
        String userId = resolveUserId(httpRequest);
        LocalDate today = LocalDate.now();
        // ...
    }
}
```

## 2. Frontend Integration

### Domain Entity
```typescript
// src/features/home/domain/entities/MedicineTaken.ts
export interface MedicineTaken {
  id: string;
  medicineId: string;
  date: Date;
  scheduledTime: string; // "08:00"
  takenAt?: Date;
  skipped: boolean;
}
```

### Repository & Use Cases
```typescript
// src/features/home/domain/repositories/MedicineTakenRepository.ts
export interface MedicineTakenRepository {
  markAsTaken(medicineId: string, date: Date, scheduledTime: string): Promise<MedicineTaken>;
  skipDose(medicineId: string, date: Date, scheduledTime: string): Promise<MedicineTaken>;
  getTodayTakes(): Promise<MedicineTaken[]>;
  getTakesForDate(date: Date): Promise<MedicineTaken[]>;
}

// src/features/home/domain/useCases/MarkMedicineAsTakenUseCase.ts
export class MarkMedicineAsTakenUseCase {
  constructor(private repository: MedicineTakenRepository) {}
  
  async execute(medicineId: string, scheduledTime: string): Promise<void> {
    await this.repository.markAsTaken(medicineId, new Date(), scheduledTime);
  }
}
```

### Enhanced Schedule Screen  
```typescript
// Update ScheduleScreen.tsx to show take/skip status
const ScheduleScreen = () => {
  const { medicines } = useMedicines();
  const { todayTakes, markAsTaken, skipDose } = useMedicineTaken();
  
  const getMedicineStatus = (medicine: Medicine, time: string) => {
    const taken = todayTakes.find(t => 
      t.medicineId === medicine.id && 
      t.scheduledTime === time
    );
    
    if (taken?.takenAt) return 'taken';
    if (taken?.skipped) return 'skipped'; 
    return 'pending';
  };
  
  // Enhanced UI with take/skip buttons
};
```

## 3. Reminder Logic

### Service for Reminder Checking
```java
// src/main/java/com/pillmind/domain/services/ReminderService.java
@Service
public class ReminderService {
    
    public List<PendingReminder> getPendingReminders(String userId, LocalDateTime now) {
        List<Medicine> medicines = medicineRepository.findAllByUserId(userId);
        LocalDate today = now.toLocalDate();
        String currentTime = now.format(DateTimeFormatter.ofPattern("HH:mm"));
        
        List<PendingReminder> pending = new ArrayList<>();
        
        for (Medicine medicine : medicines) {
            // Skip if medicine not active today
            if (today.isBefore(medicine.startDate()) || 
                (medicine.endDate() != null && today.isAfter(medicine.endDate()))) {
                continue;
            }
            
            for (String scheduledTime : medicine.times()) {
                // Check if it's time for this dose (within reminder window)
                if (isTimeForReminder(currentTime, scheduledTime)) {
                    // Check if not already taken/skipped
                    Optional<MedicineTaken> taken = medicineTakenRepository
                        .findByMedicineAndDateAndTime(medicine.id(), today, scheduledTime);
                    
                    if (taken.isEmpty()) {
                        pending.add(new PendingReminder(medicine, scheduledTime, today));
                    }
                }
            }
        }
        
        return pending;
    }
    
    private boolean isTimeForReminder(String currentTime, String scheduledTime) {
        // Logic to check if we should remind (e.g., exact time or 5min window)
        return currentTime.equals(scheduledTime);
    }
}
```

## 4. API Usage Examples

### Frontend API Calls
```typescript
// Mark as taken
await apiService.post(`/api/medicines/${medicineId}/doses/take`, {
  date: '2026-03-26',
  scheduledTime: '08:00',
  takenAt: new Date().toISOString()
});

// Skip dose  
await apiService.post(`/api/medicines/${medicineId}/doses/skip`, {
  date: '2026-03-26',
  scheduledTime: '14:00'
});

// Get today's takes
const todayTakes = await apiService.get('/api/medicines/doses/today');
```

## 5. Benefits of This Approach

✅ **Simple Implementation** - Builds on existing Medicine entity  
✅ **Clear Data Model** - One record per scheduled dose  
✅ **Flexible Tracking** - Supports taken vs skipped states  
✅ **Historical Data** - Complete audit trail  
✅ **Schedule Integration** - Works perfectly with existing ScheduleScreen  
✅ **Reminder Ready** - Easy to check what needs reminding  

## 6. Migration Strategy

1. **Phase 1**: Implement MedicineTaken backend (entity, repo, API)
2. **Phase 2**: Update frontend to consume new APIs  
3. **Phase 3**: Add take/skip buttons to ScheduleScreen
4. **Phase 4**: Implement reminder notification service
5. **Phase 5**: Add analytics (adherence rates, missed doses)

This approach gives you a solid foundation for medicine tracking while keeping complexity manageable!