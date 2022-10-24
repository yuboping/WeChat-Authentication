define(function() {
	function trim(str) {
		var i = 0;
		while ((i < str.length)
				&& ((str.charAt(i) == " ") || (str.charAt(i) == "��"))) {
			i++;
		}
		var j = str.length - 1;
		while ((j >= 0) && ((str.charAt(j) == " ") || (str.charAt(j) == "��"))) {
			j--;
		}
		if (i > j)
			return "";
		else
			return str.substring(i, j + 1);
	}

	return {
		trim : trim,
	};
});