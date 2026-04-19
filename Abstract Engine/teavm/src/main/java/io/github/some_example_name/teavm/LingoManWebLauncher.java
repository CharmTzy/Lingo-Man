    public boolean quit() {
        try {
            window.close();
            return true;
        } catch (Exception e) {
            // handle exception if necessary
            return false;
        }
    }