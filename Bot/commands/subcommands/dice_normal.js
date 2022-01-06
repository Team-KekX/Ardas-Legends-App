const dice_roller = require('../../utils/dice_roller');
const utilities = require('../../utils/utilities');

module.exports = {
    async execute(interaction) {
        const roll=interaction.options.getInteger('roll');
        const times=interaction.options.getInteger('times');
        await interaction.deferReply();
        let end_result='```[';
        let dice_roll=0;
        for (let i = 0; i < times; i++) {
            dice_roll=dice_roller.dice_roll(roll);
            if (dice_roll === 1){
                dice_roll = 'Critical Failure!';
            }
            if (dice_roll === roll){
                dice_roll = 'Critical Success!';
            }
            end_result+=dice_roll+', ';
        }
        end_result = end_result.slice(0, -2);
        end_result+=']```';
        await interaction.editReply('Result:');
        end_result=utilities.separate_long_text(end_result, false);
        for (let i=0; i<end_result.length ; i++){
            await interaction.followUp(end_result[i]);
        }
    },
};