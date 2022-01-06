
module.exports = {
    dice_roll(max)
{
    const min = 1;
    max = Math.floor(max);
    return Math.floor(Math.random() * (max - min + 1)) + min;
}
};

