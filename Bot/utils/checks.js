function isRegionValid(region) {
    if (region.includes('.S')) {
        const region_arr = region.split('.');
        region = Number(region_arr[0]);
        if(Number.isInteger(region)){
            return 1<= region <=38;
        }
    }
    else {
        region = Number(region);
        if (Number.isInteger(region)) {
            return 1 <= parseInt(region) <= 325;
        }
    }
    return false;
}

module.exports = {
    isRegionValid,

};