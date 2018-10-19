export interface Locale {
    use24HourTimeFormatByDefault(): boolean;
    anErrorOccuredWhenGeneratingTheExpressionD(): string;
    everyMinute(): string;
    everyHour(): string;
    atSpace(): string;
    everyMinutebetweenX0AndX1(): string;
    at(): string;
    spaceAnd(): string;
    everysecond(): string;
    everyX0Seconds(): string;
    secondsX0ThroughX1PastTheMinute(): string;
    atX0SecondsPastTheMinute(): string;
    atX0SecondsPastTheMinuteGt20(): string;
    everyX0Minutes(): string;
    minutesX0ThroughX1PastTheHour(): string;
    atX0MinutesPastTheHour(): string;
    atX0MinutesPastTheHourGt20(): string;
    everyX0Hours(): string;
    betweenX0AndX1(): string;
    atX0(): string;
    commaEveryDay(): string;
    commaEveryX0daysOfTheWeek(): string;
    commaX0ThroughX1(): string;
    commaMonthX0ThroughMonthX1(): string;
    commaYearX0ThroughYearX1(): string;
    first(): string;
    second(): string;
    third(): string;
    fourth(): string;
    fifth(): string;
    commaOnThe(): string;
    spaceX0OfTheMonth(): string;
    lastDay(): string;
    commaOnTheLastX0OfTheMonth(): string;
    commaOnlyOnX0(): string;
    commaEveryX0Months(): string;
    commaOnlyInX0(): string;
    commaOnTheLastDayOfTheMonth(): string;
    commaOnTheLastWeekdayOfTheMonth(): string;
    firstWeekday(): string;
    weekdayNearestDayX0(): string;
    commaOnTheX0OfTheMonth(): string;
    commaEveryX0Days(): string;
    commaBetweenDayX0AndX1OfTheMonth(): string;
    commaOnDayX0OfTheMonth(): string;
    spaceAndSpace(): string;
    commaEveryMinute(): string;
    commaEveryHour(): string;
    commaEveryX0Years(): string;
    commaStartingX0(): string;
    daysOfTheWeek(): string[];
    monthsOfTheYear(): string[];
}