export default {

	levels : [ "ALL", "TRACE", "DEBUG", "INFO", "WARN", "ERROR", "OFF" ],

	level : "INFO",

	error : function(msg) {
		if (this.isErrorEnabled()) {
			console.error(msg)
		}
	},
	warn : function(msg) {
		if (this.isWarnEnabled()) {
			console.warn(msg)
		}
	},
	info : function(msg) {
		if (this.isInfoEnabled()) {
			console.log("INFO: " + msg)
		}
	},
	debug : function(msg) {
		if (this.isDebugEnabled()) {
			console.log("DEBUG: " + msg)
		}
	},
	trace : function(msg) {
		if (this.isTraceEnabled()) {
			console.log("TRACE: " + msg)
		}
	},

	isErrorEnabled : function() {
		return this.isEnabled("ERROR");
	},
	isWarnEnabled : function() {
		return this.isEnabled("WARN");
	},
	isInfoEnabled : function() {
		return this.isEnabled("INFO");
	},
	isDebugEnabled : function() {
		return this.isEnabled("DEBUG");
	},
	isTraceEnabled : function() {
		return this.isEnabled("TRACE");
	},
	isEnabled : function(statementLevel) {
		return this.levels.indexOf(this.level) <= this.levels.indexOf(statementLevel);
	}
}