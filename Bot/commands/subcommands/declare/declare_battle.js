
module.exports = {
    async execute(interaction) {
        let target=interaction.options.getString('target').toLowerCase();
        let attacker=interaction.options.getString('attacker-list').toLowerCase();
        let defender=interaction.options.getString('defender-list').toLowerCase();
        let warcamp_coordinates=interaction.options.getString('war-camp-coordinates').toLowerCase();

        //split the above strings into arrays of strings
        //whenever a blank space is encountered

        const arr_attacker = attacker.split(" ");
        const arr_defender = defender.split(" ");
        const arr_target = target.split(" ");

        //loop through each element of the array and capitalize the first letter.


        for (let i = 0; i < arr_attacker.length; i++) {
            arr_attacker[i] = arr_attacker[i].charAt(0).toUpperCase() + arr_attacker[i].slice(1);
        }
        for (let i = 0; i < arr_defender.length; i++) {
            arr_defender[i] = arr_defender[i].charAt(0).toUpperCase() + arr_defender[i].slice(1);
        }
        for (let i = 0; i < arr_target.length; i++) {
            arr_target[i] = arr_target[i].charAt(0).toUpperCase() + arr_target[i].slice(1);
        }

        //Join all the elements of the array back into a string
        //using a blankspace as a separator
        attacker = arr_attacker.join(" ");
        defender = arr_defender.join(" ");
        target = arr_target.join(" ");

        const attacker_list = attacker.split(",");
        const defender_list = defender.split(",");
        const author = interaction.user.username;
        if (target.equals('Fieldbattle')){
            await interaction.reply(`@${author} declared a field battle. The attackers will consist of ${attacker}
                fighting against ${defender}.`);
        }
        else{
            let warcamp = '';
            if (!(warcamp_coordinates==='')){
                warcamp = ` The attackers will be using a war camp built at ${warcamp_coordinates}`;
            }
            await interaction.reply(`@${author} declared a battle on ${target}. The attackers will consist of
                ${attacker} fighting against ${defender}.${warcamp}`);
        }
    },
};