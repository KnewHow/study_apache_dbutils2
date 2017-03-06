package org.apache.commons.dbutils;

/**
 * Configuration options for {@link java.sql.Statement} when preparing statement in
 * <code>QueryRunner</code>
 * 
 * @author ygh 2017年1月14日
 */
public class StatementConfiguration {

    private final Integer fetchDirection;

    private final Integer fetchSize;

    private final Integer maxFieldSize;

    private final Integer maxRows;

    private final Integer queryTimeout;

    /**
     * Constructor for <code>StatementConfiguration</code>
     * 
     * @param fetchDirection The direction for fetching rows from database tables
     * @param fetchSize The number of rows that should be fetched from database when more rows be
     *        needed
     * @param maxFieldSize The maximum number of bytes that can be returned for character and binary
     *        column values
     * @param maxRows The maximum number of rows that a <code>ResultSet</code> produce
     * @param queryTimeout The number of seconds the driver will wait for execution
     */
    public StatementConfiguration(Integer fetchDirection, Integer fetchSize, Integer maxFieldSize,
            Integer maxRows, Integer queryTimeout) {
        this.fetchDirection = fetchDirection;
        this.fetchSize = fetchSize;
        this.maxFieldSize = maxFieldSize;
        this.maxRows = maxRows;
        this.queryTimeout = queryTimeout;
    }

    /**
     * Get fetch direction
     * 
     * @return The direction to fetch or null if not set
     */
    public Integer getFetchDirection() {
        return fetchDirection;
    }

    /**
     * Whether fetch direction is set
     * 
     * @return true is set, false otherwise
     */
    public boolean isFetchDirectionSet() {
        return fetchDirection != null;
    }

    /**
     * Get fetch size
     * 
     * @return The fetch size or null if not set
     */
    public Integer getFetchSize() {
        return fetchSize;
    }

    /**
     * Whether fetch size is set
     * 
     * @return true is set, false otherwise
     */
    public boolean isFetchSizeSet() {
        return fetchSize != null;
    }

    /**
     * Get max field size or null if not set
     * 
     * @return The max field size or null if not set
     */
    public Integer getMaxFieldSize() {
        return maxFieldSize;
    }

    /**
     * Whether the max fetch size is null
     * 
     * @return true is set, false otherwise
     */
    public boolean isMaxFieldSizeSet() {
        return maxFieldSize != null;
    }

    /**
     * Get max rows
     * 
     * @return The max rows or null if not set
     */
    public Integer getMaxRows() {
        return maxRows;
    }

    /**
     * Whether the max row is set
     * 
     * @return true if set, false otherwise
     */
    public boolean isMaxRowsSet() {
        return maxRows != null;
    }

    /**
     * Get query timeout
     * 
     * @return The query timeout or null if not set
     */
    public Integer getQueryTimeout() {
        return queryTimeout;
    }

    /**
     * Whether the query timeout is set
     * 
     * @return true if set, false otherwise
     */
    public boolean isQueryTimeoutSet() {
        return queryTimeout != null;
    }

    /**
     *  Builder for <code>StatementConfiguration</code> is more flexible constructions. 
     * @author ygh
     * 2017年1月14日
     */
    public static final class Builder {
        private Integer fetchDirection;

        private Integer fetchSize;

        private Integer maxFieldSize;

        private Integer maxRows;

        private Integer queryTimeout;
        
        public Builder fetchDirection(final Integer fetchDirection){
            this.fetchDirection = fetchDirection;
            return this;
        }
        public Builder fetchSize(final Integer fetchSize){
            this.fetchSize = fetchSize;
            return this;
        }
        public Builder maxFieldSize(final Integer maxFieldSize){
            this.maxFieldSize = maxFieldSize;
            return this;
        }
        public Builder maxRows(final Integer maxRows){
            this.maxRows = maxRows;
            return this;
        }
        public Builder queryTimeout(final Integer queryTimeout){
            this.queryTimeout = queryTimeout;
            return this;
        }
        
        public StatementConfiguration build(){
            return new StatementConfiguration(fetchDirection, fetchSize, maxFieldSize, maxRows, queryTimeout);
        }

    }

}
