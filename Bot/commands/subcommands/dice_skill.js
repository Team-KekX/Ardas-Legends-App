const dice_roller = require('../../utils/dice_roller');

module.exports = {
    // TO BE EXPANDED
    async execute(interaction) {
        const name=interaction.options.getString('name');
        const dice_roll=dice_roller.dice_roll(20);
        await interaction.reply(name+' rolled: '+dice_roll);
    },
};