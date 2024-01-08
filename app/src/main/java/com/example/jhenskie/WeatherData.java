package com.example.jhenskie;

public class WeatherData {
    private MainInfo main;
    private WeatherInfo[] weather;

    public MainInfo getMain() {
        return main;
    }

    public void setMain(MainInfo main) {
        this.main = main;
    }

    public WeatherInfo[] getWeather() {
        return weather;
    }

    public void setWeather(WeatherInfo[] weather) {
        this.weather = weather;
    }

    public static class MainInfo {
        private double temp;

        public double getTemp() {
            return temp;
        }

        public void setTemp(double temp) {
            this.temp = temp;
        }

        public String getWeatherCondition() {
            // Define weather conditions based on temperature ranges
            // Modify these ranges as per OpenWeatherMap's weather condition codes
            if (temp < 0) {
                return "5"; // Code for Snow
            } else if (temp >= 0 && temp < 10) {
                return "4"; // Code for Freezing conditions
            } else if (temp >= 10 && temp < 20) {
                return "3"; // Code for Cold conditions
            } else if (temp >= 20 && temp < 30) {
                return "2"; // Code for Clouds
            } else if (temp >= 30 && temp < 40) {
                return "1"; // Code for Rain
            } else {
                return "0"; // Code for Clear Sky
            }
        }
    }

    public static class WeatherInfo {
        private String main;
        private String description;

        public String getMain() {
            return main;
        }

        public void setMain(String main) {
            this.main = main;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
