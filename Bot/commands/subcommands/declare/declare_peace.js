
module.exports = {
    async execute(interaction) {
        let attacker=interaction.options.getString('attacker-faction');
        let defender=interaction.options.getString('defender-faction');

        //split the above strings into arrays of strings
        //whenever a blank space is encountered

        const arr_attacker = attacker.split(" ");
        const arr_defender = defender.split(" ");

        //loop through each element of the array and capitalize the first letter.


        for (let i = 0; i < arr_attacker.length; i++) {
            arr_attacker[i] = arr_attacker[i].charAt(0).toUpperCase() + arr_attacker[i].slice(1);
        }
        for (let i = 0; i < arr_defender.length; i++) {
            arr_defender[i] = arr_defender[i].charAt(0).toUpperCase() + arr_defender[i].slice(1);
        }

        //Join all the elements of the array back into a string
        //using a blankspace as a separator
        attacker = arr_attacker.join(" ");
        defender = arr_defender.join(" ");
        await interaction.reply(`@${attacker} made peace with @${defender}`);
    },
};