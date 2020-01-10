package org.qinarmy.foundation.convert;

import org.qinarmy.foundation.util.ArrayUtils;
import org.qinarmy.foundation.util.Assert;
import org.qinarmy.foundation.util.StringUtils;
import org.qinarmy.foundation.util.TimeUtils;
import org.springframework.core.convert.converter.Converter;

import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.qinarmy.foundation.util.ArrayUtils.*;


/**
 * created  on 2018/9/8.
 */
public abstract class Converters {


    public static final Set<Converter<?, ?>> CONVERTER_SET;


    static {
        Set<Converter<?, ?>> set = new HashSet<>();

        set.add( StringToLocalTime.INSTANCE );
        set.add( StringToLocalDate.INSTANCE );
        set.add( StringToLocalDatetime.INSTANCE );
        set.add( StringToMonthDay.INSTANCE );

        set.add( StringToYearMonth.INSTANCE );
        set.add( StringToZonedDatetime.INSTANCE );
        set.add( StringToLocalTimeArray.INSTANCE );
        set.add( StringToLocalDateArray.INSTANCE );

        set.add( StringToYearMonthArray.INSTANCE );
        set.add( StringToMonthDayArray.INSTANCE );
        set.add( StringToLocalDatetimeArray.INSTANCE );
        set.add( StringToZonedDateTimeArray.INSTANCE );

        CONVERTER_SET = Collections.unmodifiableSet( set );
    }

    public static class StringToLocalTime implements Converter<String, LocalTime> {

        public static final Converter<String, LocalTime> INSTANCE = new StringToLocalTime();

        private StringToLocalTime() {
        }

        @Override
        public LocalTime convert(String source) {
            LocalTime time;
            try {
                time = StringUtils.hasText( source )
                        ? LocalTime.parse( source, TimeUtils.TIME_FORMATTER ) : null;
            } catch (DateTimeParseException e) {
                time = null;
            }
            return time;
        }

    }

    public static class StringToLocalDate implements Converter<String, LocalDate> {

        public static final Converter<String, LocalDate> INSTANCE = new StringToLocalDate();

        private StringToLocalDate() {
        }

        @Override
        public LocalDate convert(String source) {
            LocalDate date;
            try {
                date = StringUtils.hasText( source )
                        ? LocalDate.parse( source, TimeUtils.DATE_FORMATTER ) : null;
            } catch (DateTimeParseException e) {
                date = null;
            }
            return date;
        }

    }

    public static class StringToLocalDatetime implements Converter<String, LocalDateTime> {

        public static final Converter<String, LocalDateTime> INSTANCE = new StringToLocalDatetime();

        private StringToLocalDatetime() {
        }

        @Override
        public LocalDateTime convert(String source) {
            LocalDateTime dateTime;
            try {
                dateTime = StringUtils.hasText( source )
                        ? LocalDateTime.parse(source, TimeUtils.DATE_TIME_FORMATTER) : null;
            } catch (DateTimeParseException e) {
                dateTime = null;
            }
            return dateTime;
        }
    }


    public static class StringToMonthDay implements Converter<String, MonthDay> {

        public static final Converter<String, MonthDay> INSTANCE = new StringToMonthDay();

        private StringToMonthDay() {
        }

        @Override
        public MonthDay convert(String source) {
            MonthDay monthDay;
            try {
                monthDay = StringUtils.hasText( source )
                        ? MonthDay.parse( source, TimeUtils.MONTH_DAY_FORMATTER ) : null;
            } catch (DateTimeParseException e) {
                monthDay = null;
            }
            return monthDay;
        }
    }


    public static class StringToYearMonth implements Converter<String, YearMonth> {

        public static final Converter<String, YearMonth> INSTANCE = new StringToYearMonth();

        private StringToYearMonth() {
        }

        @Override
        public YearMonth convert(String source) {
            YearMonth yearMonth;
            try {
                yearMonth = StringUtils.hasText( source )
                        ? YearMonth.parse( source, TimeUtils.YEAR_MONTH_FORMATTER ) : null;
            } catch (DateTimeParseException e) {
                yearMonth = null;
            }
            return yearMonth;
        }
    }

    public static class StringToZonedDatetime implements Converter<String, ZonedDateTime> {

        public static final Converter<String, ZonedDateTime> INSTANCE = new StringToZonedDatetime();

        private StringToZonedDatetime() {
        }

        @Override
        public ZonedDateTime convert(String source) {
            ZonedDateTime dateTime;
            try {
                dateTime = StringUtils.hasText( source )
                        ? ZonedDateTime.parse( source, TimeUtils.ZONE_DATE_TIME_FORMATTER ) : null;
            } catch (DateTimeParseException e) {
                dateTime = null;
            }

            return dateTime;
        }
    }


    /*###########################################   数组转换器  #########################################*/

    public static class StringToLocalTimeArray implements Converter<String, LocalTime[]> {

        public static final Converter<String, LocalTime[]> INSTANCE = new StringToLocalTimeArray();



        @Override
        public LocalTime[] convert(String source) {
            String[] sourceArray = StringUtils.hasText( source ) ? source.split( "," ) : ArrayUtils.EMPTY_STRING_ARRAY;

            LocalTime[] times = sourceArray.length == 0 ? EMPTY_TIME : new LocalTime[sourceArray.length];
            try {
                for (int i = 0; i < times.length; i++) {
                    times[i] = StringToLocalTime.INSTANCE.convert(sourceArray[i]);
                }
            } catch (IllegalArgumentException e) {
                times = EMPTY_TIME;
            }
            return times;
        }
    }

    public static class StringToLocalDateArray implements Converter<String, LocalDate[]> {

        public static final Converter<String, LocalDate[]> INSTANCE = new StringToLocalDateArray();



        @Override
        public LocalDate[] convert(String source) {
            String[] sourceArray = StringUtils.hasText( source ) ? source.split( "," ) : ArrayUtils.EMPTY_STRING_ARRAY;

            LocalDate[] dates = sourceArray.length == 0 ? EMPTY_DATE : new LocalDate[sourceArray.length];

            try {
                for (int i = 0; i < dates.length; i++) {
                    dates[i] = StringToLocalDate.INSTANCE.convert(sourceArray[i]);
                    Assert.notNull(dates[i], "");
                }
            } catch (IllegalArgumentException e) {
                dates = EMPTY_DATE;
            }

            return dates;
        }
    }


    public static class StringToYearMonthArray implements Converter<String, YearMonth[]> {

        public static final Converter<String, YearMonth[]> INSTANCE = new StringToYearMonthArray();



        @Override
        public YearMonth[] convert(String source) {
            String[] sourceArray = StringUtils.hasText( source ) ? source.split( "," ) : ArrayUtils.EMPTY_STRING_ARRAY;

            YearMonth[] dates = sourceArray.length == 0 ? EMPTY_YEAR_MONTH : new YearMonth[sourceArray.length];

            try {
                for (int i = 0; i < dates.length; i++) {
                    dates[i] = StringToYearMonth.INSTANCE.convert(sourceArray[i]);
                    Assert.notNull(dates[i], "");
                }
            } catch (IllegalArgumentException e) {
                dates = EMPTY_YEAR_MONTH;
            }

            return dates;
        }
    }

    public static class StringToMonthDayArray implements Converter<String, MonthDay[]> {

        public static final Converter<String, MonthDay[]> INSTANCE = new StringToMonthDayArray();



        @Override
        public MonthDay[] convert(String source) {
            String[] sourceArray = StringUtils.hasText( source ) ? source.split( "," ) : ArrayUtils.EMPTY_STRING_ARRAY;

            MonthDay[] dates = sourceArray.length == 0 ? EMPTY_MONTH_DAY : new MonthDay[sourceArray.length];

            try {
                for (int i = 0; i < dates.length; i++) {
                    dates[i] = StringToMonthDay.INSTANCE.convert(sourceArray[i]);
                    Assert.notNull(dates[i], "");
                }
            } catch (IllegalArgumentException e) {
                dates = EMPTY_MONTH_DAY;
            }

            return dates;
        }
    }


    public static class StringToLocalDatetimeArray implements Converter<String, LocalDateTime[]> {

        public static final Converter<String, LocalDateTime[]> INSTANCE = new StringToLocalDatetimeArray();



        @Override
        public LocalDateTime[] convert(String source) {
            String[] sourceArray = StringUtils.hasText( source ) ? source.split( "," ) : ArrayUtils.EMPTY_STRING_ARRAY;

            LocalDateTime[] dateTimes = sourceArray.length == 0 ? EMPTY_DATE_TIME
                    : new LocalDateTime[sourceArray.length];

            try {
                for (int i = 0; i < dateTimes.length; i++) {
                    dateTimes[i] = StringToLocalDatetime.INSTANCE.convert(sourceArray[i]);
                    Assert.notNull(dateTimes[i], "");
                }
            } catch (IllegalArgumentException e) {
                dateTimes = EMPTY_DATE_TIME;
            }

            return dateTimes;
        }
    }

    public static class StringToZonedDateTimeArray implements Converter<String, ZonedDateTime[]> {

        public static final Converter<String, ZonedDateTime[]> INSTANCE = new StringToZonedDateTimeArray();



        @Override
        public ZonedDateTime[] convert(String source) {
            String[] sourceArray = StringUtils.hasText( source ) ? source.split( "," ) : ArrayUtils.EMPTY_STRING_ARRAY;

            ZonedDateTime[] dateTimes = sourceArray.length == 0 ? EMPTY_ZONE_DATE_TIME
                    : new ZonedDateTime[sourceArray.length];

            try {
                for (int i = 0; i < dateTimes.length; i++) {
                    dateTimes[i] = StringToZonedDatetime.INSTANCE.convert(sourceArray[i]);
                    Assert.notNull(dateTimes[i], "");
                }
            } catch (IllegalArgumentException e) {
                dateTimes = EMPTY_ZONE_DATE_TIME;
            }

            return dateTimes;
        }
    }




}
