package org.chaos.fx.cnbeta.net;

/**
 * @author Chaos
 *         2015/11/03.
 */
public class CnBetaUtil {

    public static String getTypeString(int type){
        return type == CnBetaApi.TYPE_COMMENTS ? "comments" : type == CnBetaApi.TYPE_COUNTER ? "counter" : "dig";
    }
}
