# 🎨 Quick Reference: UI Components & Features

## Visual Component Map

```
┌──────────────────────────────────────────────────────────────┐
│ 🔁 Execution Details              [Auto-refresh ☑️] [← Back] │ ← STICKY HEADER
├──────────────────────────────────────────────────────────────┤
│                                                              │
│ ╔════════════════════════════════════════════════════════╗  │
│ ║  📊 tenantCreation           🔁 [ROLLED_BACK]          ║  │
│ ║                                                        ║  │
│ ║  ID: c5fedf20... | Type: SEQUENTIAL | Initiator: ... ║  │ ← SUMMARY CARD
│ ║  Started: 2025-11-03 20:10:24 | Duration: 146ms      ║  │
│ ║  Progress: [██░░░░] 0%                                ║  │
│ ║  Retry: Max 3 | Backoff: 5s                          ║  │
│ ║  Steps: [0✓] [1✗] [1🔁] [2 Total]                   ║  │
│ ╚════════════════════════════════════════════════════════╝  │
│                                                              │
│ ╔════════════════════════════════════════════════════════╗  │
│ ║  🎨 Status Legend                                      ║  │ ← STATUS LEGEND
│ ║  [✓Success] [✗Failed] [🔁RolledBack] [⏳Running]      ║  │
│ ╚════════════════════════════════════════════════════════╝  │
│                                                              │
│ ┌────────────────────────┬─────────────────────────────────┐│
│ │ 📈 Step Flow           │ 🕐 Timeline                     ││
│ │ (Horizontal Scroll)    │ (Vertical Scroll)               ││
│ │                        │                                 ││
│ │ ┌──────┐    ┌──────┐  │ 🟢 ORCHESTRATION_STARTED        ││
│ │ │  ① DO│ →  │  ② DO│  │ 🔵 STEP_STARTED: createRealm    ││
│ │ │create│    │create│  │ 🔴 ROLLBACK_COMPLETED           ││
│ │ │ Realm│    │Client│  │ 🔵 STEP_STARTED: createClient   ││
│ │ │      │    │      │  │ 🟡 STEP_FAILED: createClient    ││
│ │ │[ROLL │    │[FAIL]│  │ 🟣 ORCHESTRATION_COMPLETED      ││
│ │ │_BACK]│    │      │  │                                 ││
│ │ │⏱ 60ms│    │⏱16ms │  │                                 ││
│ │ │Retry:│    │Retry:│  │                                 ││
│ │ │░░ 0/3│    │░░ 0/3│  │                                 ││
│ │ └──────┘    └──────┘  │                                 ││
│ │ ← scroll →             │ ↓ scroll ↓                      ││
│ └────────────────────────┴─────────────────────────────────┘│
└──────────────────────────────────────────────────────────────┘
```

---

## Component Breakdown

### 1. Sticky Header
- **Title**: Emoji + "Execution Details"
- **Controls**: Auto-refresh toggle + Back button
- **Breadcrumbs**: Navigation trail
- **Stays visible**: When scrolling down

### 2. Summary Card (Main Info)
```
┌─────────────────────────────────────────┐
│ 📊 Orchestration Name    🔁 [STATUS]    │
│                                         │
│ ID: xxx | Type: XXX | Initiator: xxx   │
│ Started: xxx | Completed: xxx          │
│ Duration: xxx | Progress: [████░░] 50% │
│ Retry: Max X | Backoff: Xms           │
│ Steps: [X✓] [X✗] [X🔁] [X Total]      │
└─────────────────────────────────────────┘
```

**Features:**
- Orchestration name as title
- Large status badge with emoji
- 3 rows of metadata
- Visual progress bar
- Color-coded step badges

### 3. Status Legend
```
┌─────────────────────────────────────────┐
│ 🎨 Status Legend                        │
│ [✓Success] [✗Failed] [🔁RolledBack]    │
│ [⏳Running] [⏸️Pending]                  │
└─────────────────────────────────────────┘
```

**Purpose:** Quick reference for status meanings

### 4. Step Flow (Horizontal)
```
┌──────────┐   →   ┌──────────┐   →   ┌──────────┐
│ ① Badge  │       │ ② Badge  │       │ ③ Badge  │
│ StepName │       │ StepName │       │ StepName │
│          │       │          │       │          │
│ [DO]     │       │ [DO]     │       │ [UNDO]   │
│ [STATUS] │       │ [STATUS] │       │ [STATUS] │
│          │       │          │       │          │
│ ⏱ Duration│      │ ⏱ Duration│      │ ⏱ Duration│
│          │       │          │       │          │
│ Retry:   │       │ Retry:   │       │ Retry:   │
│ [███░] 2/3│      │ [░░░] 0/3│      │ [██░] 1/3│
│          │       │          │       │          │
│ Worker: xx│      │ Worker: xx│      │ Worker: xx│
└──────────┘       └──────────┘       └──────────┘
```

**Each Step Card Contains:**
- ① Step number badge (top-left)
- Step name (header)
- 🔁 Rollback icon (if triggered)
- Operation type badge (DO/UNDO)
- Status badge (color-coded)
- Duration (formatted)
- Retry progress bar (visual)
- Worker service name
- ⚠️ Error badge (if failed) with tooltip

**Arrows:** Show flow direction (→)

### 5. Timeline (Vertical)
```
┌─────────────────────────────────────┐
│ 🟢 2025-11-03 20:10:24             │
│ [ORCHESTRATION_STARTED]            │
│ Orchestration: tenantCreation      │
├─────────────────────────────────────┤
│ 🔵 2025-11-03 20:10:24             │
│ [STEP_STARTED]                     │
│ Step: createRealm                  │
│ Worker: null                       │
├─────────────────────────────────────┤
│ 🔴 2025-11-03 20:10:24             │
│ [ROLLBACK_COMPLETED]               │
│ Step: createRealm                  │
│ Status: [ROLLED_BACK]              │
├─────────────────────────────────────┤
│ 🟡 2025-11-03 20:10:24             │
│ [STEP_FAILED]                      │
│ Step: createClient                 │
│ Status: [FAILED]                   │
│ ⚠️ Reason: Connection timeout       │
├─────────────────────────────────────┤
│ 🟣 2025-11-03 20:10:24             │
│ [ORCHESTRATION_COMPLETED]          │
│ Status: [ROLLED_BACK]              │
└─────────────────────────────────────┘
```

**Each Timeline Event:**
- 🟢🔵🟡🔴🟣 Emoji icon
- Timestamp
- Event name badge (color-coded)
- Related step (if any)
- Status badge (if any)
- Details/reason text
- Hover effect

---

## Color Coding

### Status Colors
| Status | Badge Color | Emoji |
|--------|-------------|-------|
| SUCCESS | Green `bg-success` | ✅ |
| FAILED | Red `bg-danger` | ❌ |
| ROLLED_BACK | Orange `bg-warning` | 🔁 |
| RUNNING | Blue `bg-info` | ⏳ |
| PENDING | Gray `bg-secondary` | ⏸️ |

### Event Colors
| Event Type | Icon | Badge |
|------------|------|-------|
| ORCHESTRATION_STARTED | 🟢 | `bg-primary` |
| STEP_STARTED | 🔵 | `bg-primary` |
| STEP_FAILED | 🟡 | `bg-danger` |
| ROLLBACK_COMPLETED | 🔴 | `bg-warning` |
| ORCHESTRATION_COMPLETED | 🟣 | `bg-secondary` |

### Operation Types
| Type | Badge Color |
|------|-------------|
| DO | Blue `bg-primary` |
| UNDO | Orange `bg-warning` |

---

## Interactive Features

### 🔄 Auto-Refresh
```
[Auto-refresh ☑️]  ← Toggle Switch
```
- ON: Polls API every 10 seconds
- OFF: Manual refresh only
- Auto-disables when complete

### 💬 Tooltips
```
┌────────────────────────┐
│ Full Execution ID      │ ← Hover over truncated ID
│ c5fedf20-daaf-4502-... │
└────────────────────────┘

┌────────────────────────┐
│ ⚠️ Error Details        │ ← Hover over error badge
│ Connection timeout     │
└────────────────────────┘
```

### 🔘 Navigation
```
[← Back]                    ← Returns to executions list
[View Orchestration]        ← Goes to orchestration details
[Breadcrumbs > > > ]       ← Navigation trail
```

---

## Responsive Breakpoints

### Desktop (≥992px)
```
┌───────────────────────────────────────┐
│ [Step Flow 66%]  │  [Timeline 33%]   │
│  Full width      │   Visible          │
└───────────────────────────────────────┘
```

### Tablet (768-991px)
```
┌─────────────────────────────┐
│ [Step Flow 100%]            │
│ ← Horizontal Scroll →       │
├─────────────────────────────┤
│ [Timeline 100%]             │
│ ↓ Vertical Scroll ↓         │
└─────────────────────────────┘
```

### Mobile (<768px)
```
┌───────────────┐
│ [Step Flow]   │
│ ← scroll →    │
├───────────────┤
│ [Timeline]    │
│ ↓ scroll ↓    │
└───────────────┘
```

---

## Animation Effects

### Fade-in
- All cards fade in on page load
- Smooth 0.5s transition
- From opacity 0 to 1

### Progress Bars
- Smooth width transitions
- 0.3s animation

### Hover Effects
- Timeline items: Background changes
- Step cards: Subtle shadow increase
- Badges: Slight scale on hover

---

## Data Formatting

### Durations
- `< 1s` → "XXXms"
- `< 1m` → "XXs"
- `< 1h` → "XXm XXs"
- `≥ 1h` → "XXh XXm"

### Dates
- Full: "YYYY-MM-DD HH:mm:ss"
- Example: "2025-11-03 20:10:24"

### IDs
- Truncated: "c5fedf20-daaf..."
- Full on hover: "c5fedf20-daaf-4502-a2d0-2103aa16dfa7"

---

## Quick Tips

✅ **Sticky Header** - Scroll down, summary stays visible  
✅ **Horizontal Scroll** - Use mouse/trackpad for step flow  
✅ **Tooltips** - Hover for full details  
✅ **Auto-refresh** - Enable for live monitoring  
✅ **Responsive** - Works on all devices  
✅ **Color Coded** - Easy visual status recognition  
✅ **Timeline** - Complete execution history  
✅ **Navigation** - Context-aware back button  

---

## Component Hierarchy

```
ExecutionDetailsPage
├── PageHeader (Sticky)
│   ├── Title + Emoji
│   ├── Auto-refresh Toggle
│   └── Back Button
├── Summary Card
│   ├── Orchestration Info
│   ├── Timing Data
│   ├── Progress Bar
│   └── Retry Policy
├── Status Legend
│   └── Status Badges
├── Main Content (Row)
│   ├── Step Flow (Col-8)
│   │   └── Step Cards (Horizontal Scroll)
│   └── Timeline (Col-4)
│       └── Event List (Vertical Scroll)
└── Loading/Error States
```

---

This comprehensive dashboard provides **everything you need** to monitor orchestration executions at a glance! 🚀

